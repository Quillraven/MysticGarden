<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tileset" tilewidth="32" tileheight="32" spacing="2" tilecount="210" columns="30">
 <image source="tiles/map.png" width="1024" height="256"/>
 <tile id="0">
  <properties>
   <property name="light_color" value="CC1100FF"/>
   <property name="light_distance" type="float" value="1.5"/>
   <property name="light_fluctuation" type="float" value="0.1"/>
   <property name="light_fluctuation_speed" type="float" value="8"/>
   <property name="light_type" value="point"/>
  </properties>
  <animation>
   <frame tileid="0" duration="200"/>
   <frame tileid="1" duration="200"/>
   <frame tileid="2" duration="200"/>
   <frame tileid="3" duration="200"/>
   <frame tileid="4" duration="200"/>
   <frame tileid="5" duration="200"/>
   <frame tileid="6" duration="200"/>
   <frame tileid="7" duration="200"/>
  </animation>
 </tile>
 <tile id="8">
  <properties>
   <property name="type" value="AXE"/>
  </properties>
 </tile>
 <tile id="10">
  <properties>
   <property name="effect_scale" type="float" value="0.5"/>
   <property name="effect_type" value="CRYSTAL"/>
   <property name="light_color" value="0022FFCC"/>
   <property name="light_distance" type="float" value="2"/>
   <property name="light_type" value="point"/>
   <property name="type" value="CRYSTAL"/>
  </properties>
  <animation>
   <frame tileid="10" duration="150"/>
   <frame tileid="11" duration="150"/>
   <frame tileid="12" duration="150"/>
   <frame tileid="13" duration="150"/>
   <frame tileid="14" duration="150"/>
   <frame tileid="15" duration="150"/>
   <frame tileid="16" duration="150"/>
   <frame tileid="17" duration="150"/>
  </animation>
 </tile>
 <tile id="18">
  <properties>
   <property name="type" value="FIRESTONE"/>
  </properties>
 </tile>
 <tile id="32">
  <properties>
   <property name="type" value="CLUB"/>
  </properties>
 </tile>
 <tile id="62">
  <properties>
   <property name="effect_offset_x" type="float" value="0.3"/>
   <property name="effect_type" value="PORTAL"/>
   <property name="type" value="PORTAL"/>
  </properties>
  <animation>
   <frame tileid="62" duration="200"/>
   <frame tileid="63" duration="200"/>
  </animation>
 </tile>
 <tile id="72">
  <animation>
   <frame tileid="72" duration="200"/>
   <frame tileid="80" duration="200"/>
   <frame tileid="81" duration="200"/>
   <frame tileid="82" duration="200"/>
   <frame tileid="83" duration="200"/>
  </animation>
 </tile>
 <tile id="86">
  <properties>
   <property name="type" value="WALL"/>
  </properties>
 </tile>
 <tile id="88">
  <properties>
   <property name="effect_offset_y" type="float" value="-0.3"/>
   <property name="effect_scale" type="float" value="0.3"/>
   <property name="effect_type" value="TORCH"/>
   <property name="light_color" value="FF2200FF"/>
   <property name="light_cone_degree" type="float" value="45"/>
   <property name="light_direction" type="float" value="270"/>
   <property name="light_distance" type="float" value="4"/>
   <property name="light_fluctuation" type="float" value="0.05"/>
   <property name="light_fluctuation_speed" type="float" value="16"/>
   <property name="light_offset_x" type="float" value="-0.6"/>
   <property name="light_offset_y" type="float" value="0"/>
   <property name="light_type" value="cone"/>
  </properties>
  <animation>
   <frame tileid="88" duration="100"/>
   <frame tileid="89" duration="100"/>
   <frame tileid="90" duration="100"/>
   <frame tileid="91" duration="100"/>
  </animation>
 </tile>
 <tile id="92">
  <properties>
   <property name="type" value="TREE"/>
  </properties>
 </tile>
 <tile id="93">
  <properties>
   <property name="type" value="WAND"/>
  </properties>
 </tile>
 <tile id="94">
  <properties>
   <property name="type" value="CHROMA_ORB"/>
  </properties>
 </tile>
</tileset>
