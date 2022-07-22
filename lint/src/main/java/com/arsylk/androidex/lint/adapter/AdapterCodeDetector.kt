package com.arsylk.androidex.lint.adapter

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiType
import com.intellij.psi.util.InheritanceUtil
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.visitor.UastVisitor

@Suppress("UnstableApiUsage")
class AdapterCodeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement?>> {
        return listOf(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {

            override fun visitCallExpression(node: UCallExpression) {
                val receiverType = node.receiverType
                val methodName = node.methodName

                if ((methodName == AdaptMethod || methodName == AdaptCustomMethod) && receiverType.isInheritor(AdaptableRecyclerAdapter)) {
                    when (methodName) {
                        AdaptMethod -> detectAdaptMissingMethods(node)
                        AdaptCustomMethod -> detectAdaptCustomMissingMethods(node)
                    }
                }
            }

            private fun detectAdaptMissingMethods(node: UCallExpression) {
                var didFind = false
                node.accept(object : UastVisitor {
                    override fun visitElement(node: UElement): Boolean {
                        if (node is UCallExpression) {
                            if (node.methodName == InflateMethod) {
                                val receiverTypeInner = node.receiverType
                                val parent = receiverTypeInner.isInheritor(AdaptableBuilder1)
                                        || receiverTypeInner.isInheritor(AdaptableBuilder2)
                                if (parent) {
                                    didFind = true
                                    return true
                                }
                            }
                        }
                        return false
                    }
                })
                if (!didFind) context.report(
                    ISSUE,
                    node,
                    context.getCallLocation(
                        call = node,
                        includeReceiver = false,
                        includeArguments = true,
                    ),
                    "Missing call to $InflateMethod { ... }"
                )
            }

            private fun detectAdaptCustomMissingMethods(node: UCallExpression) {
                var didFind = false
                node.accept(object : UastVisitor {
                    override fun visitElement(node: UElement): Boolean {
                        if (node is UCallExpression) {
                            if (node.methodName == PrepareHolderMethod) {
                                val receiverTypeInner = node.receiverType
                                val parent = receiverTypeInner.isInheritor(AdaptableBuilder3)
                                if (parent) {
                                    didFind = true
                                    return true
                                }
                            }
                        }
                        return false
                    }
                })
                if (!didFind) context.report(
                    ISSUE,
                    node,
                    context.getCallLocation(
                        call = node,
                        includeReceiver = false,
                        includeArguments = true,
                    ),
                    "Missing call to $PrepareHolderMethod { ... }"
                )
            }
        }
    }



    private fun PsiType?.isInheritor(baseClass: String) =
        InheritanceUtil.isInheritor(this, baseClass)

    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        @JvmField
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id = "MissingAdaptMethods",
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription = "DSL builder pattern required arguments enforcing",
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation = "My Explanation 123", // no need to .trimIndent(), lint does that automatically
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                AdapterCodeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}

const val AdaptableRecyclerAdapter = "com.arsylk.androidex.lib.ui.adapter.AdaptableRecyclerAdapter"
const val AdaptableBuilder1 = "com.arsylk.androidex.lib.ui.adapter.AdaptableView.Builder"
const val AdaptableBuilder2 = "com.arsylk.androidex.lib.ui.adapter.AdaptableBinding.Builder"
const val AdaptableBuilder3 = "com.arsylk.androidex.lib.ui.adapter.AdaptableCustom.Builder"
const val AdaptMethod = "adapt"
const val InflateMethod = "inflate"
const val AdaptCustomMethod = "adaptCustom"
const val PrepareHolderMethod = "prepareHolder"
