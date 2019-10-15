#!/bin/bash
# Results in:
# ssh -A -t -i <path to ssh-key, e.g. ~/.ssh/id_rsa> -L 8080:localhost:8080 -L
# 11001:localhost:11001 -L 11002:localhost:11002 -L 11003:localhost:11003 -L
# 11004:localhost:11004 -L 11005:localhost:11005 <r-number>@st.cs.kuleuven.be ssh
# -L 8080:192.168.104.76:80 -L 11001:192.168.104.76:11001 -L
# 11002:192.168.104.76:11002 -L 11003:192.168.104.76:11003 -L
# 11004:192.168.104.76:11004 -L 11005:192.168.104.76:11005 -N -o
# StrictHostKeyChecking=no <r-number>@<name of machine, e.g.
# peer>.cs.kotnet.kuleuven.be

SSHKEY="~/.ssh/id_rsa"
PORTSTART=10926
PORTEND=10930
ENDPOINT="192.168.104.76"
STUDENTNR="r0640219"
HOST="couvin"

SSHCOMMAND="ssh -A -t -i ${SSHKEY} -L 8080:localhost:18080 "
for PORT in $(seq ${PORTSTART} ${PORTEND})
do
    SSHCOMMAND+="-L ${PORT}:localhost:${PORT} "
done
SSHCOMMAND+="${STUDENTNR}@st.cs.kuleuven.be ssh -L 18080:${ENDPOINT}:80 "
for PORT in $(seq ${PORTSTART} ${PORTEND})
do
    SSHCOMMAND+="-L ${PORT}:${ENDPOINT}:${PORT} "
done
SSHCOMMAND+="-N -o StrictHostKeyChecking=no
${STUDENTNR}@${HOST}.cs.kotnet.kuleuven.be"

echo ${SSHCOMMAND}
