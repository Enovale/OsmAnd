package net.osmand.osm;

public enum RouteActivityType {
	WATER("Water", "yellow", "mx_special_kayak", "activity_type_water"),
	WINTER("Winter", "yellow", "mx_special_skiing", "activity_type_winter"),
	SNOWMOBILE("Snowmobile", "yellow", "mx_special_snowmobile", "activity_type_snowmobile"),
	RIDING("Riding", "yellow", "mx_special_horse", "activity_type_riding"),
	RACING("Racing", "yellow", "mx_raceway", "activity_type_racing"),
	MOUNTAINBIKE("Mountainbike", "blue", "mx_sport_cycling", "activity_type_mountainbike"),
	CYCLING("Cycling", "blue", "mx_special_bicycle", "activity_type_cycling"),
	HIKING("Hiking", "orange", "mx_special_trekking", "activity_type_hiking"),
	RUNNING("Running", "orange", "mx_running", "activity_type_running"),
	WALKING("Walking", "orange", " mx_special_walking", "activity_type_walking"),
	OFFROAD("Off-road", "yellow", "mx_special_offroad", "activity_type_offroad"),
	MOTORBIKE("Motorbike", "green", "mx_special_motorcycle", "activity_type_motorbike"),
	CAR("Car", "green", "mx_shop_car", "activity_type_car");
	// less specific bottom order

	String name;
	String color;
	String icon;
	String title;

	RouteActivityType(String nm, String clr, String icon, String title) {
		this.name = nm;
		this.color = clr;
		this.icon = icon;
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public String getIcon() {
		return icon;
	}

	public String getTitle() {
		return title;
	}

	public static RouteActivityType getTypeFromName(String name) {
		for (RouteActivityType rat : values()) {
			if (rat.name().equalsIgnoreCase(name)) {
				return rat;
			}
		}
		return null;
	}

	public static RouteActivityType getTypeFromTags(String[] tags) {
		RouteActivityType activityType = null;
		for (String tg : tags) {
			RouteActivityType rat = RouteActivityType.convertFromOsmGPXTag(tg);
			if (rat != null) {
				if (activityType == null || activityType.ordinal() > rat.ordinal()) {
					activityType = rat;
				}
			}
		}
		return activityType;
	}

	public static RouteActivityType convertFromOsmGPXTag(String tg) {
		String t = tg.toLowerCase();
		if ("mountain hiking".equalsIgnoreCase(t)) {
			return HIKING;
		}
		if ("motorcar".equalsIgnoreCase(t)) {
			return CAR;
		}
		if ("laufen".equalsIgnoreCase(t)) {
			return RUNNING;
		}
		if ("pedestrian".equalsIgnoreCase(t)) {
			return WALKING;
		}
		switch (t) {
		case "mountainbiking":
		case "mtb":
		case "mountainbike":
		case "mountain bike":
		case "mountain biking":
		case "mountbarker":
		case "mtb-tour":
		case "ciclismo-mtb-gravel":
		case "vtt":
		case "btt":
		case "vth":
		case "mtb ride":
			return MOUNTAINBIKE;
		case "hiking":
		case "route=hiking":
		case "mountain hiking":
		case "hiking trail":
		case "wandern":
		case "hike":
		case "randonnée":
		case "trekking":
		case "climbing":
			return HIKING;
		case "bike":
		case "biking":
		case "bicycling":
		case "bicycle":
		case "cycling":
		case "cycle":
		case "cycleway":
		case "cykel":
		case "handcycle":
		case "cyclotourisme":
		case "route=bicycle":
		case "cyclotourism":
		case "fietsen":
		case "вело":
		case "велосипед":
		case "rower":
		case "trasa rowerem":
		case "vélo":
		case "velo":
		case "radtour":
		case "bici":
		case "fiets":
		case "fahrrad":
		case "ncn":
		case "icn":
		case "lcn":
		case "network=ncn":
		case "network=icn":
		case "network=lcn":
			return CYCLING;
		case "car":
		case "motorcar":
		case "by car":
		case "auto":
		case "автомобиль":
		case "automobile":
		case "autós":
		case "driving":
		case "drive":
		case "van":
		case "авто":
		case "на автомобиле":
		case "bus":
		case "truck":
		case "taxi":
			return CAR;
		case "running":
		case "run":
		case "rungis":
		case "trail running":
		case "trailrunning":
		case "бег":
		case "laufen":
		case "langlauf":
		case "lauf":
		case "course":
		case "jogging":
		case "fitotrack":
			return RUNNING;
		case "wanderung":
		case "walking":
		case "walk":
		case "nightwalk":
		case "walkway":
		case "пешком":
		case "пеший":
		case "pěšky":
		case "marche":
		case "pedestrian":
		case "foot":
		case "footing":
		case "on_foot":
		case "byfoot":
		case "onfoot":
		case "sightseeing":
		case "geocaching":
		case "etnanatura":
		case "etna":
		case "iwn":
		case "lwn":
		case "rwn":
		case "network=iwn":
		case "network=lwn":
		case "network=rwn":
			return WALKING;
		case "ling-moto":
		case "motorbiking":
		case "motorcycle":
		case "motorrad":
		case "motorbike":
		case "motor bike":
		case "FVbike":
		case "Motorrad":
			return MOTORBIKE;
		case "offroad":
		case "off-road":
		case "off road":
		case "4x4":
		case "terrain":
		case "quad":
		case "enduro":
		case "feldwege":
		case "feldweg":
			return OFFROAD;
		case "boat": 
		case "water": 
		case "boating": 
		case "kayak": 
		case "river": 
		case "lake": 
		case "lakes": 
		case "canal": 
			return WATER;
		case "ski": 
		case "skiing": 
		case "skating": 
		case "skitour": 
		case "winter": 
		case "wintersports": 
		case "snowboard": 
		case "лыжи": 
		case "лыжня": 
		case "nordic": 
		case "piste": 
			return WINTER;
		case "snowmobile=designated":
		case "snowmobile=permissive":
		case "snowmobile=yes":
		case "snowmobile":
			return SNOWMOBILE;
		case "ride":
		case "horse":
		case "horse trail":
			return RIDING;
		case "racing":
			return RACING;
		}
		return null;
	}
}