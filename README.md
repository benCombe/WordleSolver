# WordleSolver

This Program is designed to solve Wordle puzzles based on a user input.
The user is given a word by the program, the user then writes a string of 0's, 1's, & 2's into the input field

'0' : The letter does NOT exist in the desired word

'1' : The letter exists in the word BUT is in the incorrect placement

'2' : The letter is in the word AND is in the correct place

TEST Function
    - Solves a given number of randomly selected words and returns a report detailing the number of guesses per word, longest number of guesses, total average, and success rate.

SET START
    - allows user to input first word to analyze.

ISSUES:
- must input a '1' for ALL letters in the guess word that are in the actual word, even if letters do not repeat.

