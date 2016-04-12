#!/usr/bin/expect -f
spawn ssh -t nishant1417@iiitd.edu.in@103.25.231.3 ssh -t nishant1417@muc.iiitd.edu.in
expect "*password:*"
send "ZE82&ODC\r"
expect "*password:*"
send "nishant1417123\r"
interact
