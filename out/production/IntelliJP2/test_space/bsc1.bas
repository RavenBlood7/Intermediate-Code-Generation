Dim n0 As Integer
n0 = 1
Dim n1 As Integer
n1 = 0
Dim n2 As Integer
n2 = n0
Dim n3 As Integer
n3 = n1
Dim n4 As Integer
n4 = n1
Dim n5 As Integer
n5 = n1
Dim s0 As String
s0 = "show"
Dim n6 As Integer
n6 = 3
Dim s1 As String
s1 = "error"
Dim s2 As String
s2 = "create"
Dim n7 As Integer
n7 = 1
Dim s3 As String
s3 = "close"
Dim n8 As Integer
n8 = 2
Dim s4 As String
s4 = "pay in"
Dim n9 As Integer
n9 = 4
Dim s5 As String
s5 = "take out"
Dim n10 As Integer
n10 = 5
/'loop'/
while0:
On (1 - ((1 - ((n2 = n2) + 1)))) Goto endwhile0
	Input "Input: ", n11
	Print 1 - ((1 - ((n11 = n7) + 1)))
	Print 1 - ((1 - ((n11 = n8) + 1)))
	Print 1 - ((1 - ((n11 = n6) + 1)))
	Print 1 - ((1 - ((n11 = n9) + 1)))
	Print 1 - ((1 - ((n11 = n10) + 1)))
	Print n7
	Print n11
	/'if statement'/
	On 1 - ((1 - ((n11 = n7) + 1))) Goto endif0
		Gosub p0
		Gosub p1
	endif0:
	/'if statement'/
	On 1 - ((1 - ((n11 = n8) + 1))) Goto endif1
		Gosub p2
		Gosub p3
	endif1:
	/'if statement'/
	On 1 - ((1 - ((n11 = n6) + 1))) Goto endif2
		Gosub p2
		Gosub p4
	endif2:
	/'if statement'/
	On 1 - ((1 - ((n11 = n9) + 1))) Goto endif3
		Gosub p2
		Gosub p5
	endif3:
	/'if statement'/
	On 1 - ((1 - ((n11 = n10) + 1))) Goto notCondition4
		Gosub p2
		Gosub p6
	Goto endif4
	notCondition4:
		Print s1
	endif4:
Goto while0
endwhile0:
End

/'proc def for p1 aka create'/
p1:
	/'if statement'/
	On 1 - ((1 - ((n3 = n1) + 1))) Goto notCondition5
		n3 = n0
		Dim s6 As String
		s6 = "welcome"
		Print s6
	Goto endif5
	notCondition5:
		s6 = "account"
		Print s6
		s6 = "already"
		Print s6
		s6 = "exists"
		Print s6
	endif5:
Return

/'proc def for p3 aka close'/
p3:
	/'if statement'/
	On 1 - (((1 - ((n3 = n0) + 1)) AND (1 - ((n4 = n1) + 1)))) Goto notCondition6
		n5 = n1
		n3 = n1
		Dim s7 As String
		s7 = "good bye"
		Print s7
		End
	Goto endif6
	notCondition6:
		Print s1
	endif6:
Return

/'proc def for p4 aka show'/
p4:
	/'if statement'/
	On 1 - ((1 - ((n3 = n0) + 1))) Goto notCondition7
		Print n4
	Goto endif7
	notCondition7:
		Print s1
	endif7:
Return

/'proc def for p5 aka in'/
p5:
	Input "Input: ", n12
	/'if statement'/
	On 1 - (((1 - ((n3 = n0) + 1)) AND (1 - ((n12 > n1) + 1)))) Goto notCondition8
		n4 = (n4 + n12)
		Dim s8 As String
		s8 = "thanks"
		Print s8
	Goto endif8
	notCondition8:
		Print s1
	endif8:
Return

/'proc def for p6 aka out'/
p6:
	Input "Input: ", n13
	/'if statement'/
	On 1 - (((1 - ((n13 > n1) + 1)) AND ((1 - ((n3 = n0) + 1)) AND 1 - (1 - ((n4 < n13) + 1))))) Goto notCondition9
		n4 = (n4 - n13)
		Dim s9 As String
		s9 = "done"
		Print s9
	Goto endif9
	notCondition9:
		Print s1
	endif9:
Return

/'proc def for p0 aka setpw'/
p0:
	Dim n14 As Integer
	n14 = 1000
	Input "Input: ", n15
	/'loop'/
	while1:
On (1 - ((1 - ((n15 < n14) + 1))	)) Goto endwhile1
		Dim s10 As String
		s10 = "not good"
		Print s10
		Input "Input: ", n15
	Goto while1
	endwhile1:
	n5 = n15
	s10 = "noted"
	Print s10
Return

/'proc def for p2 aka askpw'/
p2:
	Dim s11 As String
	s11 = "1st try"
	Print s11
	Input "Input: ", n16
	/'if statement'/
	On 1 - (((1 - ((n16 > n1) + 1)) AND (1 - ((n16 = n5) + 1)))) Goto notCondition10
		s11 = "ok"
		Print s11
	Goto endif10
	notCondition10:
		s11 = "2nd try"
		Print s11
		Input "Input: ", n16
		/'if statement'/
		On 1 - (((1 - ((n16 > n1) + 1)) AND (1 - ((n16 = n5) + 1)))) Goto notCondition11
			s11 = "ok"
			Print s11
		Goto endif11
		notCondition11:
			s11 = "3rd try"
			Print s11
			Input "Input: ", n16
			/'if statement'/
			On 1 - (((1 - ((n16 > n1) + 1)) AND (1 - ((n16 = n5) + 1)))) Goto notCondition12
				s11 = "ok"
				Print s11
			Goto endif12
			notCondition12:
				Print s1
				End
			endif12:
		endif11:
	endif10:
Return

