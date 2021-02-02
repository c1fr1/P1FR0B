# Setup

### Dependencies

- python (3.5 or over)
- [dotabase](https://github.com/mdiller/dotabase) `pip install dotabase --user`
### Getting Sound Files

Use [SteamDB's VRF (Valve Resource Format) tool](https://github.com/SteamDatabase/ValveResourceFormat)
to extract mp3s from the dota game files in
`Steam/steamapps/common/dota 2 beta/game/core/dota/pak01_dir.vpk`. Within the VPK, all the hero voice
lines are stored in `sounds/vo/`. Move the extracted `sounds/vo` directory to this directory, and
`read-res.py` will populate a `responses/` directory in the format that the `ResponseModule` expects.
Note that the `responses/` directory should still be moved to `resources/` for the module to work
correctly.