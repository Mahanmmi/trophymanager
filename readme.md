# Trophy Manager

<a href="https://www.curseforge.com/minecraft/mc-mods/trophymanager/files"><img src="https://img.shields.io/badge/Available%20for-MC%201.16.5+-c70039" alt="Supported Versions"></a>
<a href="https://github.com/JDKDigital/trophymanager/blob/master/LICENSE"><img src="https://img.shields.io/github/license/JDKDigital/productive-bees?style=flat&color=900c3f" alt="License"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/trophymanager"><img src="http://cf.way2muchnoise.eu/short_trophymanager.svg" alt="Curseforge Downloads"></a><br><br>

This mod let's you define your own trophies and hand then out as quest rewards.



You can make a trophy for any entity and any item in the game including modded ones. The mod does not include a way to obtain trophies by default, you will need to either add your own recipes or use a mod like FTB Quests to give out custom trophy items.



A trophy is made with a trophymanager:trophy item and a number of NBT tags.

Here's an example of the NBT required for a trophy with a wooden hoe

```
{
    "TrophyType": "item",
    "TrophyItem": {
       "id": "minecraft:wooden_hoe"
    },
    "Name": "Super Hoe Trophy"
}
```

and an example of a big creeper

```
{
    "TrophyType": "entity",
    "TrophyEntity": {
        "entityType": "minecraft:creeper"
    },
    "Scale": 2.0,
    "OffsetY": 1.2,
    "Name": "Creeper Trophy"
}
```

More recipe examples can be found in the datapack file linked with each release.