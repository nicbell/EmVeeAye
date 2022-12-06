package net.nicbell.emveeaye

import android.content.res.Resources
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

/**
 * UIString: Allows switching between strings and string resources without additional
 * logic in the view
 */
sealed class UIString : Parcelable {
    @Parcelize
    data class ActualString(val value: String) : UIString()

    @Parcelize
    data class StringResource(@StringRes val id: Int) : UIString()

    /**
     * Only used in your view.
     */
    fun getString(resources: Resources) = when (this) {
        is ActualString -> this.value
        is StringResource -> resources.getString(this.id)
    }
}

/**
 * Helper directly from resources
 */
fun Resources.getString(uiString: UIString) = uiString.getString(this)