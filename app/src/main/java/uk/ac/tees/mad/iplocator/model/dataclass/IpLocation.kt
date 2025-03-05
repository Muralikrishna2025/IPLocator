package uk.ac.tees.mad.iplocator.model.dataclass

import com.google.gson.annotations.SerializedName

data class IpLocation(
    val ip: String,
    val type: String,
    @SerializedName("continent_code")
    val continentCode: String,
    @SerializedName("continent_name")
    val continentName: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("region_code")
    val regionCode: String,
    @SerializedName("region_name")
    val regionName: String,
    val city: String,
    val zip: String,
    val latitude: Double,
    val longitude: Double,
    val msa: String,
    val dma: String,
    val radius: Any?,
    @SerializedName("ip_routing_type")
    val ipRoutingType: Any?,
    @SerializedName("connection_type")
    val connectionType: Any?,
    val location: Location,
    @SerializedName("time_zone")
    val timeZone: TimeZone,
    val currency: Currency,
    val connection: Connection,
)

data class Location(
    @SerializedName("geoname_id")
    val geonameId: Long,
    val capital: String,
    val languages: List<Language>,
    @SerializedName("country_flag")
    val countryFlag: String,
    @SerializedName("country_flag_emoji")
    val countryFlagEmoji: String,
    @SerializedName("country_flag_emoji_unicode")
    val countryFlagEmojiUnicode: String,
    @SerializedName("calling_code")
    val callingCode: String,
    @SerializedName("is_eu")
    val isEu: Boolean,
)

data class Language(
    val code: String,
    val name: String,
    val native: String,
)

data class TimeZone(
    val id: String,
    @SerializedName("current_time")
    val currentTime: String,
    @SerializedName("gmt_offset")
    val gmtOffset: Long,
    val code: String,
    @SerializedName("is_daylight_saving")
    val isDaylightSaving: Boolean,
)

data class Currency(
    val code: String,
    val name: String,
    val plural: String,
    val symbol: String,
    @SerializedName("symbol_native")
    val symbolNative: String,
)

data class Connection(
    val asn: Long,
    val isp: String,
    val sld: String,
    val tld: String,
    val carrier: String,
    val home: Any?,
    @SerializedName("organization_type")
    val organizationType: Any?,
    @SerializedName("isic_code")
    val isicCode: Any?,
    @SerializedName("naics_code")
    val naicsCode: Any?,
)
