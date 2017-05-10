Dim userInput As String
Dim userInput2 As String
Input userInput
Input "enter2 ", userInput2

On (userInput = userInput2) + 1 Goto isNotEqual
	Print userInput ; " and" ; userInput2  ; " are equal"
gosub after
isNotEqual:
	Print userInput ; " and" ; userInput2 ; " are not equal"
	/' this 
	is 
	a comm
	ment
	'/
after:
Return