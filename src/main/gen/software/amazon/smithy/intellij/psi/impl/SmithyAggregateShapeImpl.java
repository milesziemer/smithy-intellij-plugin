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
import software.amazon.smithy.intellij.ext.SmithyPsiImplUtilKt;

public class SmithyAggregateShapeImpl extends SmithyShapeImpl implements SmithyAggregateShape {

  public SmithyAggregateShapeImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SmithyVisitor visitor) {
    visitor.visitAggregateShape(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SmithyVisitor) accept((SmithyVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SmithyShapeBody getBody() {
    return findNotNullChildByClass(SmithyShapeBody.class);
  }

  @Override
  @NotNull
  public List<SmithyMember> getMembers() {
    return SmithyPsiImplUtilKt.getMembers(this);
  }

}
