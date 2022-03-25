// This is a generated file. Not intended for manual editing.
package software.amazon.smithy.intellij.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static software.amazon.smithy.intellij.psi.SmithyTypes.*;
import software.amazon.smithy.intellij.psi.*;
import software.amazon.smithy.intellij.SmithyShapeReference;

public class SmithyShapeIdImpl extends SmithyPrimitiveImpl implements SmithyShapeId {

  public SmithyShapeIdImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SmithyVisitor visitor) {
    visitor.visitShapeId(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SmithyVisitor) accept((SmithyVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SmithyMemberName getMemberName() {
    return findChildByClass(SmithyMemberName.class);
  }

  @Override
  @Nullable
  public SmithyNamespaceId getNamespaceId() {
    return findChildByClass(SmithyNamespaceId.class);
  }

  @Override
  @NotNull
  public SmithyShapeName getShapeName() {
    return findNotNullChildByClass(SmithyShapeName.class);
  }

  @Override
  public @NotNull String getId() {
    return SmithyPsiImplUtil.getId(this);
  }

  @Override
  public @NotNull String getName() {
    return SmithyPsiImplUtil.getName(this);
  }

  @Override
  public @NotNull SmithyShapeId setName(String newName) {
    return SmithyPsiImplUtil.setName(this, newName);
  }

  @Override
  public @NotNull SmithyShapeName getNameIdentifier() {
    return SmithyPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public int getTextOffset() {
    return SmithyPsiImplUtil.getTextOffset(this);
  }

  @Override
  public @NotNull String getNamespace() {
    return SmithyPsiImplUtil.getNamespace(this);
  }

  @Override
  public SmithyShapeReference getReference() {
    return SmithyPsiImplUtil.getReference(this);
  }

  @Override
  public @NotNull String toString() {
    return SmithyPsiImplUtil.toString(this);
  }

}
