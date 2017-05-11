#!/bin/bash

for i in $(seq 0 99); do

    DIR=iteration-$(printf '%03i' $i)

    mkdir -p data/$DIR

    rm -f data/stimuli
    ln -s $DIR data/stimuli

    java -jar Runnable/target/TLS-Attacker-1.2.jar -loglevel ERROR multi_fuzzer -startup_command_file config-multifuzzer-openssl.xml | tee data/stimuli/log.txt

    rm -f data/stimuli

done

