package org.netherald.quantium

interface DataBaseUtil {

    // category is like table and collection

    fun setValue(category : String, id : String, valueName : String, value : Any)

    fun deleteValue(category : String, id : String, valueName : String, value : Any)

    fun deleteCategory(category : String)

}