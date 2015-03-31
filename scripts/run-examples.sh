#!/bin/bash

cd "$( dirname "${BASH_SOURCE[0]}" )"
cd -P ..

P=src/main/resources/examples

for F in src/main/resources/examples/*.trig; do
  F=${F%.*}; F=${F##*/}
  echo "ASCII plots for $F.trig" > $P/$F.txt
  echo >> $P/$F.txt

  for Q in src/main/resources/queries/*.sparql; do
    Q=${Q%.*}; Q=${Q##*/}
    echo "$Q plot:" >> $P/$F.txt
    echo >> $P/$F.txt
    scripts/Run.sh -p $Q $P/$F.trig >> $P/$F.txt
    echo "" >> $P/$F.txt
  done

done
