package com.infoa2.core.widget


import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.infoa2.core.widget.interfaces.ComponentActions
import com.infoa2.core.widget.interfaces.FieldValidator

import java.util.ArrayList

/**
 * Created by caio on 23/02/17.
 */

object WidgetUtils {

    private val TAG = WidgetUtils::class.java!!.getName()
    internal lateinit var appCompatActivity: AppCompatActivity
    private var fieldValidators: MutableList<FieldValidator>? = null

    fun init(appCompatActivity: AppCompatActivity) {
        WidgetUtils.appCompatActivity = appCompatActivity
        fieldValidators = ArrayList()
        findAllFieldValidators(appCompatActivity.findViewById<View>(android.R.id.content) as ViewGroup)

    }

    fun init(viewGroup: ViewGroup) {
        fieldValidators = ArrayList()
        findAllFieldValidators(viewGroup)

    }

    private fun findAllFieldValidators(viewGroup: ViewGroup) {
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            if (view is ViewGroup && view !is FieldValidator)
                findAllFieldValidators(view)
            else if (view is FieldValidator && view.visibility != View.GONE) {
                fieldValidators!!.add(view as FieldValidator)

            }
        }

    }

    fun validateComponents(): Boolean? {
        var isValid: Boolean? = java.lang.Boolean.TRUE
        for (fieldValidator in fieldValidators!!) {
            fieldValidator.isValid().let {
                if (it) {
                    isValid = java.lang.Boolean.FALSE
                    Log.d("Widget Validator", fieldValidator.toString())
                }
            }
        }
        return isValid

    }

    fun validateComponents(scrollView: ScrollView): Boolean? {
        var firstInvalidComponent: Any? = null
        for (fieldValidator in fieldValidators!!) {
            fieldValidator.isValid().let {
                if (!it) {
                    firstInvalidComponent = fieldValidator
                }

            }
        }
        if (firstInvalidComponent != null) {
            try {
                (firstInvalidComponent as EditText).parent.requestChildFocus(
                    firstInvalidComponent as View?,
                    firstInvalidComponent as View?
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return if (firstInvalidComponent == null) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE

    }

    fun cleanComponents(components: List<ComponentActions>) {
        for (componentActions in components) {
            componentActions.cleanComponent()
        }
    }

    fun disableAndCleanComponents(components: List<ComponentActions>, afterComponent: ComponentActions) {
        val index = components.indexOf(afterComponent)
        val componentsFiltered = components.subList(index, components.size)
        for (componentActions in componentsFiltered) {
            componentActions.cleanComponent()
            componentActions.enableComponent(false)

        }
    }

    fun disableComponents(components: List<ComponentActions>, afterComponent: ComponentActions) {
        val index = components.indexOf(afterComponent)
        val componentsFiltered = components.subList(index, components.size)
        for (componentActions in componentsFiltered) {
            componentActions.enableComponent(false)
        }
    }
}
