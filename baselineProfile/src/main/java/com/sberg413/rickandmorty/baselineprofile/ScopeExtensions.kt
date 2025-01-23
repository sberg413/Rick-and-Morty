package com.sberg413.rickandmorty.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until


fun MacrobenchmarkScope.waitForAsyncContent() {
    // wait for the main character list to appear
    device.wait(Until.hasObject(By.res("character_list")), 5_000)
    // grab a reference to the character list
    val contentList = device.findObject(By.res("character_list"))
    // Wait until a snack collection item within the list is rendered.
    contentList.wait(Until.hasObject(By.res("character_item")), 5_000)
}

fun MacrobenchmarkScope.scrollSnackListJourney() {
    val snackList = device.findObject(By.res("character_list"))
    // Set gesture margin to avoid triggering gesture navigation.
    snackList.setGestureMargin(device.displayWidth / 5)
    snackList.fling(Direction.DOWN)
    device.waitForIdle()
}

fun MacrobenchmarkScope.goToCharacterDetailJourney() {
    val snackList = device.findObject(By.res("character_list"))
    val characters = snackList.findObjects(By.res("character_item"))
    // Select character from the list based on running iteration.
    val index = (iteration ?: 0) % characters.size
    characters[index].click()
    // Wait until the screen is gone = the detail is shown.
    device.wait(Until.hasObject(By.res("character_detail")), 5_000)
}