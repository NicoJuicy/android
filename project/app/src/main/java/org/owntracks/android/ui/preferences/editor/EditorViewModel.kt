package org.owntracks.android.ui.preferences.editor

import android.os.Bundle
import androidx.databinding.Bindable
import org.owntracks.android.BR
import org.owntracks.android.R
import org.owntracks.android.data.repos.WaypointsRepo
import org.owntracks.android.injection.scopes.PerActivity
import org.owntracks.android.support.Parser
import org.owntracks.android.support.Preferences
import org.owntracks.android.ui.base.viewmodel.BaseViewModel
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@PerActivity
class EditorViewModel @Inject constructor(
    private val preferences: Preferences,
    private val parser: Parser
) : BaseViewModel<EditorMvvm.View?>(), EditorMvvm.ViewModel<EditorMvvm.View?> {
    @get:Bindable
    @Bindable
    override var effectiveConfiguration: String? = null
        private set

    @JvmField
    @Inject
    var waypointsRepo: WaypointsRepo? = null
    override fun attachView(savedInstanceState: Bundle?, view: EditorMvvm.View?) {
        super.attachView(savedInstanceState, view!!)
        updateEffectiveConfiguration()
    }

    private fun updateEffectiveConfiguration() {
        try {
            val message = preferences.exportToMessage()
            message.waypoints = waypointsRepo!!.exportToMessage()
            message[preferences.getPreferenceKey(R.string.preferenceKeyPassword)] = "********"
            setEffectiveConfiguration(parser.toJsonPlainPretty(message))
        } catch (e: IOException) {
            Timber.e(e)
            view!!.displayLoadFailed()
        }
    }

    @Bindable
    private fun setEffectiveConfiguration(effectiveConfiguration: String) {
        this.effectiveConfiguration = effectiveConfiguration
        notifyPropertyChanged(BR.effectiveConfiguration)
    }

    override fun onPreferencesValueForKeySetSuccessful() {
        updateEffectiveConfiguration()
        notifyPropertyChanged(BR.effectiveConfiguration)
    }
}