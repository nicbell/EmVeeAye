# ♻️ EmVeeAye [![](https://jitpack.io/v/net.nicbell/emveeaye.svg)](https://jitpack.io/#net.nicbell/emveeaye)
Some kinda MVI, heavily inspired by everything but with much less stuff.

## 🙋🏽 Why
I wanted a YAGNI approach to MVI and unidirectional data flow using coroutines `StateFlow`. I tend to handle the intent, process the action and perform state production in the view model, then just test the sequence of emission from the view model. You won’t find any state handler or reducer classes here. Of course if you use this library and like to add those things, by all means enjoy yourself.

## 🪜 Setup
Include the dependency in your project.
```groovy
implementation "net.nicbell.emveeaye:emveeaye:x.x.x"
```
In order to download the dependency please make sure access to the Maven repository is configured. You can use JitPack or GitHub.

<details>
  <summary>From JitPack</summary>

JitPack is full configured to work with the [custom domain](https://jitpack.io/#net.nicbell/emveeaye/latest). Just included the Maven repository and add the dependency as described above.
```gradle
maven { url 'https://jitpack.io' }
```

</details>

<details>
  <summary>From GitHub</summary>
  
This repo is public but GitHub's Maven Repository needs authentication.
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
</details>

## 🏄🏽 Usage

Intents, states and events (side-effects) are sealed classes. View Model receives intents and performs an action that emits states and events.

```kotlin
class MyViewModel : MVIViewModel<MyIntent, MyState, MyEvent>(MyState.Empty) {
    
    // You will need to implement `onIntent` to handle all your intents
    override fun onIntent(intent: MainIntent) = when (intent) {
        MyIntent.LoadContent -> loadContentAction()
        MyIntent.DoSomething -> doSomethingAction()
    }

    // This action can only run in MyState.Empty
    private fun loadContentAction() = actionOn<MyState.Empty> {
        setState(MyState.Loaded(emptyList()))
    }

    // This action can run in any state
    private fun doSomethingAction() = action {
        sendEvent(MyEvent.Error("I don't want to do anything."))
    }
}
```

## 🔬 Testing

Include the dependency in your project.
```groovy
testImplementation "net.nicbell.emveeaye:emveeaye-test:x.x.x"
```

```kotlin
class MyViewModelTest : ViewModelTest() {

    private val vm = MyViewModel()

    @Test
    fun myTest() = runTest {
        // WHEN
        vm.onIntent(MyIntent.LoadContent)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            MyState.Empty,
            MyState.Loaded(emptyList())
        )
    }

    @Test
    fun myTest2() = runTest {
        // WHEN
        vm.onIntent(MyIntent.DoSomething)

        // THEN
        merge(vm.state, vm.events).assertFlow(
            MyState.Empty,
            MyEvent.Error("I don't want to do anything.")
        )
    }
}
```
