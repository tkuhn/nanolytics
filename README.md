Nanolytics
==========

Analytics tools for nanopublications.

_(this is work in progress...)_


Cardinal Assertion Analytics: Design Sketches
---------------------------------------------

### Agent-Focused View

Simple:

    o o o  3 nanopublications
    | | |
    o o o  3 processes
    |/  |
    o   o  2 agents (2 bots)
    |   |
    o   o  2 codebases

Bots and humans:

    o o o  3 nanopublications
    | | |
    o o |  2 processes
    |/  |
    o   o  2 agents (1 bot; 1 human)
    |
    o      1 codebase: https://github.com/example/my-bot

Many nanopubs:

    o o o o o o o o o o o o o  13 nanopublications
    | | | | | | | | | |/  | |
    o o o o o o o o o o   o o  12 processes
    |/_/_/_/_/_/_/_/  |   |/
    o                 o   o     3 agents (3 bots)
    |                 |   |
    o                 o   o     3 codebases


### Data-Focused View

Simple:

    o o o  3 nanopublications
    | | |
    o o o  3 sources
    |/  |
    o   o  2 primary sources
    |   |
    o   o  2 primary agents

Long:

    o o o  3 nanopublications
    | | |
    o o o  3 sources
    |/  |
    o   |  1 intermediate source
    |   |
    o   |  1 intermediate source
    |   |
    o   |  1 intermediate source
    |   |
    o   o  2 primary sources
    |   |
    o   o  2 primary agents

Unknown primary sources:

    o o o  3 nanopublications
    | | |
    o o o  3 sources
    |/  |
    o   ?  1 primary source
    |
    ?      0 primary agent

Intermixed sources:

    o o o o o  5 nanopublications
    | | | | |
    o o o o o  5 sources
    XXXXXXX |
    o o     o  3 primary sources
    | |     |
    o o     o  3 primary agents
