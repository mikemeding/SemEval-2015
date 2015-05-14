# SemEval-2015
Making an attempt at SemEval 2015 task 1 

Hoanh and I decided to take a crack at the SemEval paraphrase evaluation task 1. We split up the work as follows, Hoanh wrote the neural network which integrated each of the word feature extrators. The final configuration of this network contained 2 hidden layers with back propogation. Our results from this are listed in the tables below. I on the other hand worked on the data analsys writing both a parser and preprocessor to feed the neural network in addition to handling all documents and references.

## Final Results (run 1) ##

### Training: ###
> tp = 2467.0, tn = 6791.0, fp = 743.0, fn = 1529.0

9258 of 11530 correctly tagged... 0.802948829141370

| measure | score |
| --------- | ---------------- |
| precision | 0.7685358255451713 |
| recall | 0.6173673673673674 |
| f1 | 0.6847071884540661 |

### Dev: ###
> tp = 640.0, tn = 2468.0, fp = 204.0, fn = 830.0

3108 of 4142 correctly tagged... 0.7503621438918396

| measure | score |
| --------- | ---------------- |
| precision | 0.7582938388625592 |
| recall | 0.43537414965986393 |
| f1 | 0.5531547104580813 |


### Test: ###
> tp = 95.0, tn = 613.0, fp = 50.0, fn = 80.0

708 of 838 correctly tagged... 0.8448687350835322

| measure | score |
| --------- | ---------------- |
| precision | 0.6551724137931034 |
| recall | 0.5428571428571428 |
| f1 | 0.5937499999999999 |

## Final Results (run 2) ##

### Training: ###
> tp = 2470.0, tn = 6769.0, fp = 765.0, fn = 1526.0

9239 of 11530 correctly tagged... 0.8013009540329575

| measure | score |
| --------- | ---------------- |
| precision | 0.7635239567233385 |
| recall | 0.6181181181181181 |
| f1 | 0.6831696860738488 |


### Dev: ###
> tp = 651.0, tn = 2466.0, fp = 206.0, fn = 819.0

3117 of 4142 correctly tagged... 0.7525350072428778

| measure | score |
| --------- | ---------------- |
| precision | 0.7596266044340724 |
| recall | 0.44285714285714284 |
| f1 | 0.559518693596906 |

### Test: ###
> tp = 94.0, tn = 621.0, fp = 42.0, fn = 81.0

715 of 838 correctly tagged... 0.8532219570405728

| measure | score |
| --------- | ---------------- |
| precision | 0.6911764705882353 |
| recall | 0.5371428571428571 |
| f1 | 0.6045016077170418 |

