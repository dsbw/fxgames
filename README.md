# fxGames

I cannot use alcohol as an excuse, but this code (and the accompanying blog ["FX My Life"][1]) are my stream-of-consciousness meander through JavaFX. It's like Jack Kerouac and William S. Burroughs writing code and talking to themselves about it! (It really isn't.)

But seriously, it ain't pretty and it don't always make sense.

My rationale is highly personal: 

1. I still like building native, desktop apps, and have actual use for same.
2. JavaFX is a big beast, but it seems well-supported by a relatively broad community of users.
3. But I'm likely to do any serious coding in Clojure.
4. Doing things like this in Java teach me JavaFX so that I can take the knowledge and go back to my real work in Clojure.
5. Working in Java generally reminds me of how much more productive life is in Clojure.
6. I mean, seriously. Java has massive ecosystems built-up to solve things that just never come up in Clojure.
   
    a. And it's good for me to experience the pain that necessitates those solutions.
7. JavaFX books are laughable.
   
    a. Actually, almost all tech books these days seem laughable.
   
    b. They teach you how to do the things you could figure out from the docs, pretty much.

    c. But when you put together an app, you run into all kinds of issues not in the books.

    d. In case other people want to learn JavaFX, I want to leave a trail that's easy to follow and hits all the issues, along with increasingly useful solutions.

As a young amateur, I coded in a very ad hoc, quick way, which worked until I ran into the limitations of my environment or my mind (or both). What this meant is that when someone said, "I have a solution to problem X," I could say, "Aha! I need a solution because I have had problem X many times!" And usually the solution (pure functions, objects, reusable libraries, etc.) was along the lines of what I had been thinking. 

So for this exercise, I've done much the same things: I've basically slapped together code however I could make it work, which served two purposes:

1. I could get a handle on the basic task (JavaFX).
2. I could run smack into the problems that need solving.

## Part I: Starters and, ugh, Tic-Tac-Toe

In this first commit, you have the complete product of the first 15 "chapters", building a tic-tac-toe game. We ran into many problems on this journey, from just getting started to the value of reactive programming. The task was building a basic starting interface, and then a tic-tac-toe game. Boring, I know, but sufficient to encounter many hurdles and quite frankly more complex a task than any book on JavaFX I have read, to date, has cared to tackle. 

Part II will have its own branch, and each commit will reflect a "chapter", as we build a more complex game, the rules of which we don't even know up front.


[1]: https://blakefx.medium.com/