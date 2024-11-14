package com.team22.soundary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.team22.soundary.databinding.ActivityMainBinding
import com.team22.soundary.feature.main.presentation.MainFragment
import com.team22.soundary.feature.profile.fragment.ProfileFragment
import com.team22.soundary.feature.search.presentation.friend.FriendSearchFragment
import com.team22.soundary.feature.share.presentation.music.ShareMusicFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        if (savedInstanceState == null) {
            replaceFragment(MainFragment())
        }
    }

    private fun setupNavigation() {
        binding.nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_main -> {
                    replaceFragment(MainFragment())
                    true
                }

                R.id.action_share -> {
                    replaceFragment(ShareMusicFragment())
                    true
                }

                R.id.action_friend -> {
                    replaceFragment(FriendSearchFragment())
                    true
                }

                R.id.action_my -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commitAllowingStateLoss()
    }

    private fun checkAndRequestPermissions(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 요청
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
}