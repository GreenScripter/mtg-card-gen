# Python Model Setup
First, run the Java data generation scripts.
## Setup Python Environment
`conda env create -f environment.yml -p ./env`
## Train Models
```
cd colorgen ; python3 train.py ; cd ..
cd costgen ; python3 train.py ; cd ..
cd namegen ; python3 train.py ; cd ..
cd namegen ; python3 train.py --from-name ; cd ..
cd rulestextgen ; python3 train.py ; cd ..
cd typegen ; python3 train.py ; cd ..```
