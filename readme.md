# ♻️ EmVeeAye

[![Maven Central](https://img.shields.io/maven-central/v/net.nicbell.emveeaye/emveeaye?color=blue)](https://s01.oss.sonatype.org/content/repositories/releases/net/nicbell/emveeaye/)
[![codecov](https://codecov.io/gh/nicbell/EmVeeAye/branch/main/graph/badge.svg?token=YYJ348RZAF)](https://codecov.io/gh/nicbell/EmVeeAye)

Some kinda MVI, heavily inspired by everything but with much less stuff.

## 🙋🏽 Why

I wanted a YAGNI approach to MVI and unidirectional data flow using coroutines `StateFlow`. Instead
of many separate classes, handling intents, producing actions, reducing and state emissions can
happen inside the view model. Then just test the sequence of emission from the view model. Of course,
if you use this library and like having lots of files and jumping around, by all means enjoy 
yourself.

<img width="640" alt="image" src="https://user-images.githubusercontent.com/151842/205030773-707063cb-666f-4eff-a790-fc2b01d22a3e.png">

#### Intents vs actions

How do these differ? What you want is not always what you get.

Here is an example.
> You're feeling a bit sleepy, your intent is to have a cup of coffee. You handle your intent by
> checking to see if there is any coffee, after that your action may be to have a coffee or not have a
> coffee. One of these actions may reduce you to a less sleepy state, the other will leave you sleepy
> as well as disappointed.

There is an Italian saying that describes the difference between intents and actions quite well.
> "**Tra il dire e il fare c’è di mezzo il mare**" – Between saying and doing, there is the sea
>
> This phrase means that it is easy to speak about something, but doing it is a whole different story.
> In other words, saying you’ll do something is not the same as actually doing it.

When an intent is received by the ViewModel, it handles the intent and generates an action based on
that intent.

In the context of Model-View-Intent (MVI) architecture, actions are responsible for updating the
state of the application, not intents.

#### Reducers

The reducer function is a pure function that takes the current state of the application and an
Action object, and returns a new state that reflects the changes made by the
Action. `(state, action) -> new state`.

The reducer function is responsible for updating the state of the application, based on the Action
produced by handling the Intent that was triggered by the user. It's called a reducer because it
takes the current state and reduces it to a new state based on the Action.

#### Side effects

I chose to follow the latest opinion from Google of a singular UI state object rather than separate
states & events (side-effects).

> UI actions that originate from the ViewModel — ViewModel events — should always result in a UI state
> update. — [Android Developer docs](https://developer.android.com/topic/architecture/ui-layer/events#handle-viewmodel-events)

## 🪜 Setup

Include the dependency in your project.

```groovy
implementation "net.nicbell.emveeaye:emveeaye:x.x.x"
```

In order to download the dependency please make sure access to the maven repo is configured.
You probably already have Maven Central configured; if you don't you will need to add it.

```gradle
repositories {
    //..
    mavenCentral()
}
```

## 🏄🏽 Usage

State is a data class. Intents and Actions are sealed classes. The View Model receives an Intent and
transforms it into an Action. The Actions is used to update the state via a reducer
function `(state, action) -> new state`.

```kotlin
class MyViewModel : MVIViewModel<MyIntent, MyState, MyAction>(
    initialState = MyState(),
    reducer = { state, action ->
        when (action) {
            is MyAction.ShowContent -> state.copy(data = action.data)
            is MyAction.ShowError -> state.copy(error = action.error)
        }
    }
) {
    // You will need to implement `onIntent` to handle all your intents
    override fun onIntent(intent: MyIntent) = when (intent) {
        MyIntent.LoadContent -> handleLoadContent()
        MyIntent.DoSomething -> handleSomethingAction()
    }

    private fun handleLoadContent() = withState {
        updateState(MyAction.ShowContent(listOf("Test")))
    }

    private fun handleSomethingAction() = withState {
        updateState(MyAction.ShowError("I don't want to do anything."))
    }
}
```

In your activity or fragment you can observe state changes.

```kotlin
class MainActivity : AppCompatActivity() {

    private val vm: MyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //..

        observeFlow(vm.state) { updateUI(it) }
    }
    //..
}
```

If you like to have separate classes for reducer functions you can use those by implementing
the `Reducer` type alias.

```kotlin
class MyStateReducer : Reducer<MyState, MyAction> {
    override fun invoke(state: MyState, action: MyAction) = when (action) {
        is MyAction.ShowContent -> state.copy(data = action.data)
        is MyAction.ShowError -> state.copy(error = action.error)
    }
}

class MyViewModel : MVIViewModel<MyIntent, MyState, MyAction>(
    initialState = MyState(),
    reducer = MyStateReducer()
) {
    //..
}
```

#### Saving state

The view model constructor accepts a `SavedStateHandle` which is `null` by default. When
a `SavedStateHandle` is supplied the state is automatically restored and saved with each state
update.

⚠️ Your state will need to implement `Parcelable` inorder to be saved by `SavedStateHandle`.

```kotlin
class MyViewModel(savedStateHandle: SavedStateHandle) : MVIViewModel<MyIntent, MyState, MyAction>(
    initialState = MyState(),
    reducer = { state, action -> /**/ },
    savedStateHandle = savedStateHandle
) {
    //..
}
```

## 🔬 Testing

Include the testing dependency in your project.

```groovy
testImplementation "net.nicbell.emveeaye:emveeaye-test:x.x.x"
```

The `ViewModelTest` class allows us to test the flow of state emitted from the view model.

```kotlin
class MyViewModelTest : ViewModelTest() {

    private val vm = MyViewModel()

    @Test
    fun myTest() = runTest {
        // WHEN
        vm.onIntent(MyIntent.LoadContent)

        // THEN
        vm.state.assertFlow(
            MyState(),
            MyState(data = listOf("Test"))
        )
    }

    @Test
    fun myTest2() = runTest {
        // WHEN
        vm.onIntent(MyIntent.DoSomething)

        // THEN
        vm.state.assertFlow(
            MyState(),
            MyState(error = "I don't want to do anything.")
        )
    }
}
```
