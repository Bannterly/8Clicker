package com.ruffian7.sevenclicker.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.ruffian7.sevenclicker.AutoClicker;

public class KeyListener implements NativeKeyListener {
	private final Set<Integer> pressedKeys = new HashSet<>();

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		//  the key text and modifiers
		String keyText = NativeKeyEvent.getKeyText(event.getKeyCode());
		String modifiersText = NativeKeyEvent.getModifiersText(event.getModifiers());

		String targetKey = AutoClicker.toggleKey[0];
		String targetModifiers = AutoClicker.toggleKey[1];

		// if the pressed key matches the toggle key
		if (keyText.equals(targetKey) && !AutoClicker.gui.focused) {
			// no modifiers are required or if the modifiers match
			if (targetModifiers.isEmpty() || modifiersText.equals(targetModifiers)) {
				AutoClicker.toggle();
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		pressedKeys.remove(event.getKeyCode());
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {
		// NO-OP
	}
}
