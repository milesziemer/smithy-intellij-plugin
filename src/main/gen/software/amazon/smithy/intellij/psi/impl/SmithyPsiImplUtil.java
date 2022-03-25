package software.amazon.smithy.intellij.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.smithy.intellij.SmithyFile;
import software.amazon.smithy.intellij.SmithyShapeReference;
import software.amazon.smithy.intellij.psi.SmithyBoolean;
import software.amazon.smithy.intellij.psi.SmithyDocumentation;
import software.amazon.smithy.intellij.psi.SmithyId;
import software.amazon.smithy.intellij.psi.SmithyKey;
import software.amazon.smithy.intellij.psi.SmithyKeyedElement;
import software.amazon.smithy.intellij.psi.SmithyMember;
import software.amazon.smithy.intellij.psi.SmithyMemberName;
import software.amazon.smithy.intellij.psi.SmithyModel;
import software.amazon.smithy.intellij.psi.SmithyNamespace;
import software.amazon.smithy.intellij.psi.SmithyNamespaceId;
import software.amazon.smithy.intellij.psi.SmithyNumber;
import software.amazon.smithy.intellij.psi.SmithyShape;
import software.amazon.smithy.intellij.psi.SmithyShapeId;
import software.amazon.smithy.intellij.psi.SmithyShapeName;
import software.amazon.smithy.intellij.psi.SmithySimpleShape;
import software.amazon.smithy.intellij.psi.SmithySimpleTypeName;
import software.amazon.smithy.intellij.psi.SmithyTrait;
import software.amazon.smithy.intellij.psi.SmithyTypes;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * A utility class providing mixins for AST nodes (generated by <a href="https://github.com/JetBrains/Grammar-Kit">Grammar-Kit</a>).
 * <p>
 * All {@link SmithyNumber} value-conversion methods perform safe parsing (by using {@link BigDecimal}) but will fail
 * with an {@link ArithmeticException} if converting to an integral integer type and the number is too large to fit.
 *
 * @author Ian Caffey
 * @since 1.0
 */
public class SmithyPsiImplUtil {
    private static final TokenSet DOCUMENTATION_LINES = TokenSet.create(SmithyTypes.TOKEN_DOCUMENTATION_LINE);

    public static boolean booleanValue(SmithyBoolean b) {
        return Boolean.parseBoolean(b.getText());
    }

    public static double byteValue(SmithyNumber number) {
        return bigDecimalValue(number).byteValueExact();
    }

    public static double shortValue(SmithyNumber number) {
        return bigDecimalValue(number).shortValueExact();
    }

    public static double intValue(SmithyNumber number) {
        return bigDecimalValue(number).intValueExact();
    }

    public static float floatValue(SmithyNumber number) {
        return bigDecimalValue(number).floatValue();
    }

    public static double doubleValue(SmithyNumber number) {
        return bigDecimalValue(number).doubleValue();
    }

    public static long longValue(SmithyNumber number) {
        return bigDecimalValue(number).longValueExact();
    }

    public static BigDecimal bigDecimalValue(SmithyNumber number) {
        return new BigDecimal(number.getText());
    }

    public static BigInteger bigIntegerValue(SmithyNumber number) {
        return bigDecimalValue(number).toBigIntegerExact();
    }

    public static PsiElement getOwner(SmithyDocumentation documentation) {
        return documentation.getParent();
    }

    @NotNull
    public static IElementType getTokenType(SmithyDocumentation documentation) {
        return SmithyTypes.DOCUMENTATION;
    }

    @NotNull
    public static String toDocString(SmithyDocumentation documentation) {
        //see: https://awslabs.github.io/smithy/1.0/spec/core/idl.html#documentation-comment
        StringJoiner joiner = new StringJoiner("\n");
        for (ASTNode child : documentation.getNode().getChildren(DOCUMENTATION_LINES)) {
            String text = child.getText();
            joiner.add(text.substring(text.length() > 3 && text.charAt(3) == ' ' ? 4 : 3));
        }
        return joiner.toString();
    }

    @NotNull
    public static String toString(SmithyId id) {
        return id.getText();
    }

    @NotNull
    public static String toString(SmithyMemberName name) {
        return name.getText();
    }

    @NotNull
    public static String toString(SmithyShapeId id) {
        StringBuilder builder = new StringBuilder();
        SmithyNamespaceId namespaceId = id.getNamespaceId();
        if (namespaceId != null) {
            builder.append(namespaceId).append(".");
        }
        builder.append(id.getShapeName());
        SmithyMemberName memberName = id.getMemberName();
        if (memberName != null) {
            builder.append("$").append(memberName);
        }
        return builder.toString();
    }

    @NotNull
    public static String toString(SmithyShapeName name) {
        return name.getText();
    }

    @NotNull
    public static String toString(SmithyNamespaceId namespaceId) {
        return namespaceId.getParts().stream().map(SmithyId::toString).collect(joining("."));
    }

    @NotNull
    public static String getName(SmithyKeyedElement element) {
        return element.getKey().getText();
    }

    @NotNull
    public static SmithyKeyedElement setName(SmithyKeyedElement element, String newName) {
        SmithyKey key = element.getKey();
        TextRange textRange = key.getTextRange();
        Document document = FileDocumentManager.getInstance().getDocument(key.getContainingFile().getVirtualFile());
        document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newName);
        PsiDocumentManager.getInstance(key.getProject()).commitDocument(document);
        return element;
    }

    @NotNull
    public static SmithyKey getNameIdentifier(SmithyKeyedElement element) {
        return element.getKey();
    }

    public static int getTextOffset(SmithyKeyedElement element) {
        return element.getKey().getTextOffset();
    }

    @NotNull
    public static String getName(SmithyMember member) {
        return member.getNameIdentifier().getText();
    }

    @NotNull
    public static SmithyMember setName(SmithyMember member, String newName) {
        SmithyMemberName name = member.getNameIdentifier();
        TextRange textRange = name.getTextRange();
        Document document = FileDocumentManager.getInstance().getDocument(name.getContainingFile().getVirtualFile());
        document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newName);
        PsiDocumentManager.getInstance(name.getProject()).commitDocument(document);
        return member;
    }

    public static int getTextOffset(SmithyMember member) {
        return member.getNameIdentifier().getTextOffset();
    }

    @NotNull
    public static String getName(SmithyShape shape) {
        return shape.getNameIdentifier().getText();
    }

    @NotNull
    public static SmithyShape setName(SmithyShape shape, String newName) {
        SmithyShapeName name = shape.getNameIdentifier();
        TextRange textRange = name.getTextRange();
        Document document = FileDocumentManager.getInstance().getDocument(name.getContainingFile().getVirtualFile());
        document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newName);
        PsiDocumentManager.getInstance(name.getProject()).commitDocument(document);
        return shape;
    }

    @NotNull
    public static SmithyShapeName getNameIdentifier(SmithyShape shape) {
        return requireNonNull(PsiTreeUtil.getChildOfType(shape, SmithyShapeName.class));
    }

    public static int getTextOffset(SmithyShape shape) {
        return shape.getNameIdentifier().getTextOffset();
    }

    @NotNull
    public static String getName(SmithyShapeId shapeId) {
        return shapeId.getNameIdentifier().getText();
    }

    @NotNull
    public static SmithyShapeId setName(SmithyShapeId shapeId, String newName) {
        SmithyShapeName name = shapeId.getNameIdentifier();
        TextRange textRange = name.getTextRange();
        Document document = FileDocumentManager.getInstance().getDocument(name.getContainingFile().getVirtualFile());
        document.replaceString(textRange.getStartOffset(), textRange.getEndOffset(), newName);
        PsiDocumentManager.getInstance(name.getProject()).commitDocument(document);
        return shapeId;
    }

    @NotNull
    public static SmithyShapeName getNameIdentifier(SmithyShapeId shapeId) {
        return shapeId.getShapeName();
    }

    public static int getTextOffset(SmithyShapeId shapeId) {
        return shapeId.getNameIdentifier().getTextOffset();
    }

    @NotNull
    public static String getTypeName(SmithySimpleShape shape) {
        return requireNonNull(PsiTreeUtil.getChildOfType(shape, SmithySimpleTypeName.class)).getText();
    }

    @Nullable
    public static SmithyDocumentation getDocumentation(SmithyShape shape) {
        return PsiTreeUtil.getChildOfType(shape, SmithyDocumentation.class);
    }

    @NotNull
    public static String getNamespace(SmithyShape shape) {
        SmithyNamespace namespace = requireNonNull(PsiTreeUtil.getChildOfType(shape.getParent(), SmithyNamespace.class));
        return namespace.getNamespaceId().getId();
    }

    @NotNull
    public static String getNamespace(SmithyShapeId shapeId) {
        SmithyNamespaceId namespaceId = shapeId.getNamespaceId();
        if (namespaceId != null) {
            return namespaceId.getId();
        }
        SmithyModel model = ((SmithyFile) shapeId.getContainingFile()).getModel();
        SmithyNamespace namespace = requireNonNull(PsiTreeUtil.getChildOfType(model, SmithyNamespace.class));
        return namespace.getNamespaceId().getId();
    }

    @NotNull
    public static List<SmithyTrait> getDeclaredTraits(SmithyShape shape) {
        return PsiTreeUtil.getChildrenOfTypeAsList(shape, SmithyTrait.class);
    }

    @NotNull
    public static List<SmithyShape> getShapes(SmithyModel model) {
        return PsiTreeUtil.getChildrenOfTypeAsList(model, SmithyShape.class);
    }

    @NotNull
    public static String getId(SmithyNamespaceId id) {
        return id.getParts().stream().map(SmithyId::getText).collect(joining("."));
    }

    @NotNull
    public static String getId(SmithyShapeId id) {
        SmithyNamespaceId namespaceId = id.getNamespaceId();
        SmithyShapeName shapeName = id.getShapeName();
        SmithyMemberName memberName = id.getMemberName();
        StringBuilder builder = new StringBuilder();
        if (namespaceId != null) {
            builder.append(namespaceId.getId()).append("#");
        }
        builder.append(shapeName.getText());
        if (memberName != null) {
            builder.append("$").append(memberName.getText());
        }
        return builder.toString();
    }

    @NotNull
    public static ItemPresentation getPresentation(SmithyMember member) {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return member.getName() + ": " + member.getShapeId().getId();
            }

            @Override
            public String getLocationString() {
                SmithyShape parentShape = (SmithyShape) member.getParent().getParent();
                return parentShape.getNamespace() + "#" + parentShape.getName();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return member.getIcon(0);
            }
        };
    }

    @NotNull
    public static ItemPresentation getPresentation(SmithyShape shape) {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return shape.getName();
            }

            @Override
            public String getLocationString() {
                return shape.getNamespace();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return shape.getIcon(0);
            }
        };
    }

    @NotNull
    public static SmithyShapeReference getReference(SmithyShapeId shapeId) {
        return new SmithyShapeReference(shapeId);
    }
}