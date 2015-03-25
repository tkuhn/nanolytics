Nanolytics
==========

Analytics tools for nanopublications.

_(this is work in progress...)_


Cardinal Assertion Analytics
----------------------------

### Design Sketches

Agent-focused view:

    o o o  3 nanopublications
    | | |
    o o o  3 processes
    \ / |
     o  o  2 agents (2 bots)
     |  |
     o  o  2 codebases


    o o o  3 nanopublications
    | | |
    o o |  2 processes
    \ / |
     o  o  2 agents (1 bot; 1 human)
     |
     o     1 codebase: https://github.com/example/my-bot


    o o o o o o o o o o o o o  13 nanopublications
    | | | | | | | | | \ / | |
    o o o o o o o o o  o  o o  12 processes
    \_\_\_\_|_/_/_/_/  |  \ /
            o          o   o   3 agents (3 bots)
            |          |   |
            o          o   o   3 codebases

Data-focused view:

    o o o o  4 nanopublications
    | | | |
    o o o o  4 sources
    \ / |
     o  o    2 primary sources
     |  |
     o  o    2 primary agents
