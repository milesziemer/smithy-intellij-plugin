// This is a generated file. Not intended for manual editing.
package software.amazon.smithy.intellij.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SmithyList extends SmithyShape, SmithyElement {

  @Nullable
  SmithyDocumentation getDocumentation();

  @NotNull
  SmithyShapeName getShapeName();

  @NotNull
  List<SmithyTrait> getDeclaredTraits();

  @NotNull
  List<SmithyMember> getMembers();

}
