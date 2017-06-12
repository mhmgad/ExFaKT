#!/usr/bin/env bash

PREFIX=/home/gadelrab/mpiRoot

#sh assemble/bin/create_exper.sh \
#$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/imdb/yago_actedIn.tsv \
#$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/gen_rules.iris \
#$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/queries.iris \
#$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/gen_facts.iris


sh assemble/bin/create_exper.sh \
$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/countryWonPrize/kg.tsv \
$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/countryWonPrize/gen_rules.iris \
$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/countryWonPrize/queries.iris \
$PREFIX/GW/D5data-7/gadelrab/fact_spotting_data/generated/countryWonPrize/gen_facts.iris
