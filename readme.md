# ♻️ EmVeeAye
Some kinda MVI, heavily inspired by everything but with much less stuff.

## 🙋🏽‍️ Why
I wanted a YAGNI approach to MVI and unidirectional data flow. You won’t find any state handler or reducer classes here. Of course if you use this library and like those things, by all means enjoy yourself.

## 🧩 Setup
Include the dependency in your project.
```groovy
implementation "net.nicbell.emveeaye:lib:x.x.x"
```

In order to download the dependency please make sure access to the GitHub Maven repository is configured. Repo is public but GitHub needs authentication anyway.
```gradle
maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/nicbell/EmVeeAye")
    credentials {
        username = github_user
        password = github_token
    }
}
```
To download EmVeeAye you will need to create a [personal access token](https://github.com/settings/tokens) with `read:packages` scope.

Please do not push your tokens to GitHub, you can store them in `local.properties` instead.
```properties
githubName="username"
githubToken="xxx"
```

## 🏎 Usage