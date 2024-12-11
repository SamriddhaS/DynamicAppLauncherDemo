package com.example.dynamicappicon

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.dynamicappicon.ui.theme.DynamicAppIconTheme
import kotlinx.coroutines.launch

/**
* Data For the app launcher icons available for the app.
* */
enum class AppLauncherIcons(val themeName:String,val imageRes:Int){
    Default_Theme(themeName = ".DefaultTheme", imageRes = R.mipmap.ic_launcher),
    Theme_One(themeName = ".MyIconOne", imageRes = R.mipmap.my_launch_icon_one),
    Theme_Two(themeName = ".MyIconTwo", imageRes = R.mipmap.my_launch_icon_two),
    Theme_Three(themeName = ".MyIconThree", imageRes = R.mipmap.my_launch_icon_three),
}

const val CURRENT_ICON_KEY = "CURRENT_ICON_KEY"
const val PREFERENCE_NAME = "MY_APP_SHARED_PREF"

class MainActivity : ComponentActivity() {

    private val sharedPref by lazy {
        applicationContext.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DynamicAppIconTheme {
                MyApp(
                    onChangeAppIcon = { icon ->
                        val currentIcon = sharedPref.getString(CURRENT_ICON_KEY,AppLauncherIcons.Default_Theme.themeName)
                        // If the selected icon is same as the current one that is already set we do nothing.
                        if (currentIcon==icon.themeName) return@MyApp
                        changeIcon(icon,currentIcon!!)
                    },
                )
            }
        }
    }

    /**
    * This function updates the current app icon to the selected icon that
     * user has choosen.
     * @param selectedIcon = this param takes the new icon that the user trying to apply.
     * We will check what is the current icon set for the app. if the choosen icon is same
     * as current one then we do nothing. Otherwise we apply the new icon and update the
     * current icon data in our shared preference.
    * */
    private fun changeIcon(selectedIcon: AppLauncherIcons,currentIcon:String) {

        // New selected icon will be set as new launcher icon.
        packageManager.setComponentEnabledSetting(
            ComponentName(
                this,
                "$packageName${selectedIcon.themeName}"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // Check if we need to disable / set default
        val disableOrSetDefault = needToDisableOrSetDefault(currentIcon)

        // Disabling old launcher icon after new one is set.
        packageManager.setComponentEnabledSetting(
            ComponentName(
                this,
                "$packageName${currentIcon}"
            ),
            disableOrSetDefault,
            PackageManager.DONT_KILL_APP
        )

        // Save the updated icon.
        sharedPref.edit {
            putString(CURRENT_ICON_KEY,selectedIcon.themeName)
            apply()
        }
    }

    /**
    * If the current alias is the default one( the activity-alias which is having this property -> android:enabled="true" )
     * we need to use COMPONENT_ENABLED_STATE_DISABLED to disable it, it will cause the app to kill.
     * If the current alias default enabled property is "false" ( activity-alias with android:enabled="false") we can use
     * COMPONENT_ENABLED_STATE_DEFAULT to reset that alias's enabled property set to false (as we have given it enabled="false" in manifest).
     * This will make sure the launcher icon change doesn't cause app kill when changing between two activity-alias
     * whose default enabled property is false (android:enabled="false").
    * */
    private fun needToDisableOrSetDefault(currentIcon:String):Int{
       return if (currentIcon==AppLauncherIcons.Default_Theme.themeName)
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        else
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    onChangeAppIcon:(AppLauncherIcons)->Unit
) {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        val bottomSheetState = rememberModalBottomSheetState()
        val coroutineScope = rememberCoroutineScope()
        var selectedImage by remember { mutableStateOf<AppLauncherIcons?>(null) }
        var showBottomSheet by remember { mutableStateOf(false) }

        ImageSelector(
            modifier = modifier,
            onImageClick = { imageRes ->
                selectedImage = imageRes
                coroutineScope.launch {
                    showBottomSheet = true
                }
            }
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = bottomSheetState,
            ) {
                selectedImage?.let {
                    BottomSheetContent(
                        imageRes = it,
                        onButtonClick = {
                            onChangeAppIcon(it)
                            showBottomSheet = false
                        }
                    )
                }
            }
        }
     }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageSelector(
    onImageClick: (AppLauncherIcons) -> Unit,
    modifier: Modifier
) {
    val imageList = listOf(
        AppLauncherIcons.Default_Theme,
        AppLauncherIcons.Theme_One,
        AppLauncherIcons.Theme_Two,
        AppLauncherIcons.Theme_Three,
    )

    var selectedImage by rememberSaveable { mutableStateOf(imageList.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(imageList.size) { index ->
                val imageRes = imageList[index]
                GlideImage(
                    model = imageRes.imageRes,
                    contentDescription = "Image $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { onImageClick(imageRes) }
                        .padding(4.dp)
                )
            }
        }

        Button(
            onClick = {
                // Handle the "Apply as App Icon" logic here
                println("Selected Image: $selectedImage")
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tap on a icon to apply as app icon.",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BottomSheetContent(imageRes: AppLauncherIcons, onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 36.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Apply this as app icon.",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        GlideImage(
            model = imageRes.imageRes,
            contentDescription = "Selected Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(150.dp)
        )
        Button(
            onClick = onButtonClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Confirm",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DynamicAppIconTheme {
        MyApp(
            onChangeAppIcon = {

            }
        )
    }

}