# GsBaseAndroid

# Cấu hình Gradle gồm 2 bước

**Step 1.** Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:
```css
        dependencyResolutionManagement {
                repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                repositories {
                    mavenCentral()
                    maven { url 'https://jitpack.io' }
                }
            }
```

**Step 2.** Add the dependency
```css
        dependencies {
                    implementation 'com.github.vtabk2:GsBaseAndroid:1.0.5'
            }
```

# Cách dùng

**Decompress**

```css
                viewModelScope.launch(Dispatchers.IO) {
                                val folderPath = ...
                                Decompress(path, folderPath).unzip(object : Decompress.Callback {
                                    override fun callbackSuccess() {
                                     
                                    }

                                    override fun callbackFailed() {
                                    
                                    }
                                })
                                File(path).delete()
                            }
```