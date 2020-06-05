
# LibGDX Tetris implementation  
  ![tetrisdemo2](https://user-images.githubusercontent.com/29984767/83883575-a6074b80-a711-11ea-82df-0debf6c10189.gif)

## Features  
- Input tuning (DAS/delayed auto shift, ARR/auto repeat rate, SDS/soft drop speed)  
- 7 bag randomizer and hold  
- Previews and ghost piece  
- Stats (PPS, APM, Attack, Lines Sent, Combo, Back to Back, TSS, TSD, TST, Single, Double, Triple, Quad/Tetris, Perfect Clear, Time)
- SRS rotation system with regular and 180 wall kick tables
	- all spins supported including 180 spins
- L1, L2, L3 lock delays:
	- L1 - lock delay from soft drop
	- L2 - lock delay from soft drop and left or right movement (reset from rotation)
	- L3 - max amount of time piece locks no matter
- Gravity 
- Garbage system
	- garbage queue maintained with red bar indicator 
	- garbage cancellation
	- receiving garbage separated by lines in garbage queue (random holes)
- Solid garbage

## Controls
- Left, Right - movement
- Down - soft drop
- Space - hard drop
- Up - clockwise rotation
- Z - counterclockwise rotation
- X - 180 rotation
- LShift - hold
- R - restart
- G - queue 1 to 6 lines of garbage
- S - add 10 lines of solid garbage
