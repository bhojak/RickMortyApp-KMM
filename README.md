# Rick and Morty Android
This is a sample Android app that implements the MVI architecture design pattern. The app consumes data from the open [Rick And Morty Api](https://rickandmortyapi.com/).

### Screenshots   


| <img width="240" src="./screenshots/characters_list.png" /> | <img width="240" src="./screenshots/character_detail.png" /> | <img width="240" src="./screenshots/characters_search.png" /> |
|-------------------------------------------------------------|--------------------------------------------------------------|---------------------------------------------------------------|

<br/>

### Technologies

*   User Interface built with **[Jetpack Compose](https://developer.android.com/jetpack/compose)**
*   A single-activity architecture, using **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**.
*   A presentation layer that contains a Compose screen (View) and a **ViewModel** per screen (or feature).
*   The networking layer that uses **[Retrofit](https://square.github.io/retrofit/)**
*   Reactive UIs using **[Flow](https://developer.android.com/kotlin/flow)** and **[coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** for asynchronous operations.
*   ViewModels integration tests using **[Turbine](https://github.com/cashapp/turbine)**
*   E2E testing using **[Compose Test Rule](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeTestRule)** and **[HiltAndroidRule](https://dagger.dev/api/latest/dagger/hilt/android/testing/HiltAndroidRule.html)**
*   Dependency injection using [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).





