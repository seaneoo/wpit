# ![WPIT (Whose Pet Is That?)](https://i.imgur.com/h20V6ci.png)

[![Available on Modrinth](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/mod/wpit) [![Available on CurseForge](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/compact/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/wpit) [![Available on GitHub](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/compact/available/github_vector.svg)](https://github.com/seaneoo/wpit/releases)

[![Requires Fabric API](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/compact/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api) [![Requires Cloth Config](https://raw.githubusercontent.com/intergrav/devins-badges/68af3da1d56294934ece854c43dac9ab1b0eb3e9/assets/compact/requires/cloth-config-api_vector.svg)](https://modrinth.com/mod/cloth-config/)

WPIT (Whose Pet Is That?) is a 1.18.2 and 1.19.x Fabric mod that allows you to easily see the owner of a pet¹. Simply
hover your crosshair over a tamed pet and the owner's name will display above it. This mod is 100% client-side, putting it on the
server will do nothing.

¹Wolves, cats, parrots, foxes, horses, donkeys, mules, and llamas.

## Downloads

WPIT has official downloads on [Modrinth](https://modrinth.com/mod/wpit), [CurseForge](https://www.curseforge.com/minecraft/mc-mods/wpit), and [GitHub](https://github.com/seaneoo/wpit/releases). Although you may find it elsewhere, these are the only two sources that will be consistently up-to-date and guaranteed safe.

### Dependencies

WPIT requires two mods to function, [Fabric API](https://modrinth.com/mod/fabric-api) and [Cloth Config](https://modrinth.com/mod/cloth-config/).

You may also add [Mod Menu](https://modrinth.com/mod/modmenu) if you would like to change the mod's options in-game.

## Options

- **Enable WPIT** If set to _yes_, the mod will function as intended. If _no_, it will not do anything.
- **Display Nameplate** The mode in which the owner nameplates will appear.
  - Hover: If you target the pet with your crosshair, the nameplate will show (similar to custom names).
  - Always: As long as you are within 64 blocks of the pet, the nameplate will show.
  - Nearby: If you are within X number of blocks of the pet, the nameplate will show (configurable amount, up to 64).
- **Nearby Distance** The number of blocks for the Nearby display mode to work (min 1, max 64).
- **Show Other Owners** If set to _yes_, more than the primary owner of the pet will show. Only used for foxes.
- **Nameplate Format** Change the format of the nameplate. Use _%s_ as a placeholder for the owner's name.
- **Nameplate Text Color** Changes the color of the owner's nameplate. If you edit this in-game, it will autocomplete valid
  inputs.

## Modpacks

As this mod is distrubuted under [The Unlicense](LICENSE), you have complete freedom to put this in your modpacks (or do
anything with it, frankly). If you do put it in a modpack, I'd love to check it out regardless!

## Screenshots

![](https://i.imgur.com/C9yePky.png)
<br>
*A wolf named "Buddy" who is tamed by player "seano".*
<br><br>

![](https://i.imgur.com/BRvYVlM.png)
<br>
*A fox with a custom name and two trusted players, "seano" and "MoozleVidaLeia".*
<br><br>

![](https://i.imgur.com/IWbnFvA.png)
<br>
*Four pets with owners/trusted players and the "Nameplate Text Color" option set to LIGHT_PURPLE.*
<br><br>
