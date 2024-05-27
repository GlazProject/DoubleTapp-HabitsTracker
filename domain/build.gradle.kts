plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("com.google.dagger:dagger:2.51.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    kapt("com.google.dagger:dagger-compiler:2.51.1")

    testImplementation("junit:junit:4.13.2")
}