import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { RootStackParamList } from '../types';

import HomeScreen from '../screens/HomeScreen';
import TableSelectionScreen from '../screens/TableSelectionScreen';
import AdvancedConfigScreen from '../screens/AdvancedConfigScreen';
import CountdownScreen from '../screens/CountdownScreen';
import GameScreen from '../screens/GameScreen';
import ResultScreen from '../screens/ResultScreen';

const Stack = createStackNavigator<RootStackParamList>();

export default function AppNavigator() {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Home"
        screenOptions={{ headerShown: false, gestureEnabled: false }}
      >
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="TableSelection" component={TableSelectionScreen} />
        <Stack.Screen name="AdvancedConfig" component={AdvancedConfigScreen} />
        <Stack.Screen name="Countdown" component={CountdownScreen} />
        <Stack.Screen name="Game" component={GameScreen} />
        <Stack.Screen name="Result" component={ResultScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
