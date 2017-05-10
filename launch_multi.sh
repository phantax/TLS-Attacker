#!/bin/bash

for i in $(seq 0 9); do

    DIR=data/iteration-$(printf '%03i' $i)

    mkdir -p $DIR

    rm -f data/stimuli
    ln -s $DIR data/stimuli

#    java -jar Runnable/target/TLS-Attacker-1.2.jar -loglevel ERROR multi_fuzzer -startup_command_file config-multifuzzer-openssl.xml

    rm -f data/stimuli

done

