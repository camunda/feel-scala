---
id: tutorial-1-3
title: "1.3 Third Stop: Even more numeric functions"
---
Our friend Camundonaut is now entering France and during the battle with Hydra lost the Boots of Hermes. It is 7:00 AM and our friend has to continue by foot. The goal is to walk to Lyon which is 729.1 kms distance.

Considering average walking speed is 5 km/h how many days would he need to walk? and if he didn't stop to rest, at what time would he reach his destination?


```
// First determine the number of days using time = distance / speed
(729.1/5) = 145 hrs

// To determine his arrival time, we can leverage modular arithmetics with the funciton modulo
(145+7)mod24 = 8:00 AM

//24 representing a 24 hour clock
```

Now, let's look at some functions you could do with strings