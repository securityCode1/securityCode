import React from 'react'
import Background from '../components/Backgroundnpm star'
import Logo from '../components/Logo2'
import Header from '../components/Header'
import Button from '../components/Button'
import BackCard from '../components/BAckCard'

export default function Dashboard({ navigation }) {
  return (
    <Background>
      <BackCard>
      
      <Header>Let’s start</Header>
      
      <Button
        mode="outlined"
        onPress={() =>
          navigation.reset({
            index: 0,
            routes: [{ name: 'StartScreen' }],
          })
        }
      >
        Logout
      </Button>
      
      <Logo />
      </BackCard>
    </Background>
  )
}
