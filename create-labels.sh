#!/bin/sh
REPO="bpg/my-awesome-library"

hub api /repos/${REPO}/labels -X POST -f 'name=dependency' -f 'description=Dependency update' -f 'color=a8f49c'
hub api /repos/${REPO}/labels -X POST -f 'name=skip-changelog' -f 'description=Do not include this PR in the changelog' -f 'color=ffffdd'
hub api /repos/${REPO}/labels -X POST -f 'name=breaking' -f 'description=Backward-incompatible change, may break existing API clients' -f 'color=e2b236'
hub api /repos/${REPO}/labels -X POST -f 'name=maintenance' -f 'description=Maintenance / technical debt' -f 'color=371596'
hub api /repos/${REPO}/labels -X POST -f 'name=major' -f 'color=666666'
hub api /repos/${REPO}/labels -X POST -f 'name=minor' -f 'color=666666'
hub api /repos/${REPO}/labels -X POST -f 'name=patch' -f 'color=666666'
