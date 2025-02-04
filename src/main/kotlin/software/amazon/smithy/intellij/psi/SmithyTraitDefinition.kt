package software.amazon.smithy.intellij.psi

import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil.appendStyledSpan
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil.getStyledSpan
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiNamedElement
import software.amazon.smithy.intellij.SmithyColorSettings
import software.amazon.smithy.intellij.SmithyShapeResolver.getDefinitions
import software.amazon.smithy.intellij.generateLink

/**
 * A [trait](https://awslabs.github.io/smithy/1.0/spec/core/model.html#traits) definition (either in an AST or IDL) in [Smithy](https://awslabs.github.io/smithy).
 *
 * @author Ian Caffey
 * @since 1.0
 * @see SmithyTrait
 * @see SmithyAstTrait
 */
interface SmithyTraitDefinition : SmithyElement, NavigatablePsiElement, PsiNamedElement {
    val href: String
        get() = resolvedNamespace?.let { namespace ->
            generateLink("${namespace}#${shapeName}", "@$shapeName", SmithyColorSettings.TRAIT_NAME)
        } ?: getStyledSpan(SmithyColorSettings.TRAIT_NAME, "@$shapeName", 1f)

    val shapeName: String
    val declaredNamespace: String?
    val resolvedNamespace: String?
    val value: SmithyValueDefinition
    fun resolve() = getDefinitions(this).firstOrNull()
    fun toDocString(): String = buildString {
        append("<span>").append(href)
        value.let { value ->
            if (value.type == SmithyValueType.OBJECT) {
                value.fields.takeIf { fields -> fields.isNotEmpty() }?.let { fields ->
                    append("(")
                    val fieldDocs = fields.map { (key, value) ->
                        buildString {
                            appendStyledSpan(this, SmithyColorSettings.SHAPE_MEMBER, key, 1f)
                            append(": ")
                            append(value.toDocString())
                        }
                    }
                    //Quick n' dirty formatting: try to fit entire object on one line if there's a single field, otherwise
                    //place each field on separate line with 4 spaces indent (each field will reside on a single line)
                    //There's not a great solution for either getting
                    //  a) formatting + correct contextual syntax highlighting in one go (we only get lexer based highlighting)
                    //  b) custom contextual syntax highlighting which produces HTML fragments that can also infer the line lengths
                    //The best compromise is the best syntax highlighting, since most traits will be small
                    //TODO: see if a custom AnnotationHolder could bridge the gap where syntax highlighting + annotations could be translated into HTML
                    if (fieldDocs.size > 1) {
                        appendLine()
                        fieldDocs.onEachIndexed { i, doc ->
                            if (i != 0) append(",").appendLine()
                            append("    ").append(doc)
                        }
                        appendLine()
                    } else {
                        fieldDocs.onEachIndexed { i, doc ->
                            if (i != 0) append(", ")
                            append(doc)
                        }
                    }
                    append(")")
                }
            } else {
                append("(").append(value.toDocString()).append(")")
            }
        }
        append("</span>")
    }
}