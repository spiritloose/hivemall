Hivemall: Hive scalable machine learning library
=================================================

Hivemall is a scalable machine learning library that runs on Apache Hive, licensed under the LGPL 2.1.  
Hivemall is designed to be scalable to the number of training instances as well as the number of training features.

![logo](https://raw.github.com/myui/hivemall/master/resources/hivemall-logo_s.png "Hivemall's cute(!?) logo")

Supported Algorithms
--------------------

Hivemall provides machine learning functionality as well as feature engineering functions 
through UDFs/UDAFs/UDTFs of Hive.

## Classfication

* Perceptron

* Passive Aggressive (PA, PA1, PA2)

* Confidence Weighted (CW)

* Adaptive Regularization of Weight Vectors (AROW)

* Soft Confidence Weighted (SCW1, SCW2)

## Regression

* Logistic Regression using Stochastic Gradient Descent
  (parameter mixing and iterative parameter mixing)
  
* Passive Aggressive Regression (PA1, PA2)

* AROW regression

## Recommendation

* Minhash (LSH with jaccard index)

## k-Nearest Neighbor

* Minhash (LSH with jaccard index)

* b-Bit minhash

* Brute-force search using cosine similarity


## Feature engineering
  
* Feature hashing (MurmurHash, SHA1)

* Feature scaling (Min-Max Normalization, Z-Score)

* Feature instances amplifier that reduces iterations on training

* Bias clause

* Data generator for one-vs-the-rest classifiers

System requirements
--------------------

**Hivemall v0.2**

* Hive 0.11 or later [Recommended, Required for compilation]

* Hive 0.9, 0.10 [Not supported, New features in v0.2 do not work]

**Hivemall v0.1**

* Hive 0.9 or later

Basic Usage
------------

```sql
SELECT 
  feature, 
  CAST(avg(weight) as FLOAT) as weight
FROM
 (SELECT logress(features,label) as (feature,weight) FROM training_features) t
GROUP BY feature;
```

Find more examples on [our wiki page](https://github.com/myui/hivemall/wiki/).

License
---------

GNU Lesser General Public License 2.1  
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

According to LGPL 2.1, softwares that modifies/copies Hivemall MUST open their source codes upon requests.  
On the other hand, softwares that uses the Hivemall jar package without any modification are no need to show their sources.
See GNU's [LGPL and Java page](http://www.gnu.org/licenses/lgpl-java.en.html) for detail.

The LGPL does not require you to distribute source code of programs as far as just using/linking Hivemall in your application, so you can use Hivemall in closed-source apps and for most commercial uses.

Copyright
---------

Copyright (C) 2013-2014 National Institute of Advanced Industrial Science and Technology (AIST)  

Put the above copyrights for the services/softwares that use Hivemall.

Support
-------

Support is through the [issue list](https://github.com/myui/hivemall/issues). Put a question label to ask a question.

References
----------

Please refer the following paper for research uses:

* Makoto Yui. ``Hivemall: Scalable Machine Learning Library for Apache Hive'', 
  [2014 Hadoop Summit](http://hadoopsummit.org/san-jose/), June 2014. \[[slide](http://www.slideshare.net/myui/hivemall-hadoop-summit-2014-san-jose)]

* Makoto Yui and Isao Kojima. ``Hivemall: Hive scalable machine learning library'' (demo paper), 
  [NIPS 2013 Workshop on Machine Learning Open Source Software: Towards Open Workflows](https://mloss.org/workshop/nips13/), Dec 2013.

* Makoto Yui and Isao Kojima. ``A Database-Hadoop Hybrid Approach to Scalable Machine Learning'',  
  Proc. IEEE 2nd International Congress on Big Data, July 2013 \[[paper](http://staff.aist.go.jp/m.yui/publications/bigdata2013myui.pdf)\] \[[slide](http://www.slideshare.net/myui/bigdata2013myui)\]

Acknowledgment
--------------

This work was supported in part by a JSPS grant-in-aid for young scientists (B) #24700111 and a JSPS grant-in-aid for scientific research (A) #24240015.

[![Analytics](https://ga-beacon.appspot.com/UA-104966-13/myui/hivemall)](https://github.com/myui/hivemall)
