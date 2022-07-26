plugins {
    id("java-library")
    id("kotlin")
    id("com.android.lint")
}

lint {
    absolutePaths = false
    ignoreTestSources = true
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
    compileOnly("com.android.tools.lint:lint-api:30.2.1")
    testImplementation("junit:junit:4.13.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}