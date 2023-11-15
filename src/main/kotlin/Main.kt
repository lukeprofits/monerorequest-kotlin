import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.random.Random


// Easy way to parse JSON
fun parseJson(jsonStr: String): MutableMap<String, Any> {
    val result = mutableMapOf<String, Any>()
    val jsonContent = jsonStr.trim().removeSurrounding("{", "}").trim()
    val keyValuePairs = jsonContent.split(",").map { it.trim() }

    for (pair in keyValuePairs) {
        val (key, value) = pair.split(":", limit = 2).map { it.trim() }
        val processedValue = when {
            value.equals("null", ignoreCase = true) -> null
            value.equals("true", ignoreCase = true) -> true
            value.equals("false", ignoreCase = true) -> false
            value.matches("-?\\d+(\\.\\d+)?".toRegex()) -> value.toDoubleOrNull() ?: value
            value.startsWith("\"") && value.endsWith("\"") -> value.removeSurrounding("\"")
            else -> value
        }
        result[key.removeSurrounding("\"")] = processedValue ?: value
    }

    return result
}


// Easy way to convert to JSON string
fun mapToJson(map: Map<String, Any>): String {
    val sortedMap = map.toSortedMap()
    return sortedMap.entries.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, value) ->
        val jsonValue = when (value) {
            is String -> "\"${escapeString(value)}\""
            is Int -> value.toString()
            else -> "\"$value\""
        }
        "\"$key\":$jsonValue"
    }
}


fun escapeString(str: String): String {
    return str.replace("\\", "\\\\") // Escape backslashes
        .replace("\"", "\\\"")   // Escape quotes
        .replace("\n", "\\n")    // Escape newlines
        .replace("\r", "\\r")    // Escape carriage returns
        .replace("\t", "\\t")    // Escape tabs
}


fun makeRandomPaymentId(): String {
    val chars: String = "0123456789abcdef"
    val paymentId = (1..16)
        .map { Random.nextInt(chars.length) }
        .map(chars::get)
        .joinToString("")
    return paymentId
}


fun convertToTruncatedRFC3339(zonedDateTime: ZonedDateTime): String {
    // Convert the input datetime to UTC. This step is crucial to standardize the time representation,
    // especially if the original datetime is in a different timezone.
    val utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)

    // Define the formatter with a pattern that matches the RFC3339 standard.
    // This pattern includes up to milliseconds ('.SSS').
    // The 'Z' in the pattern is a literal character, representing UTC time.
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    // Format the datetime in UTC according to the specified pattern.
    // The resulting string includes the date, time, and milliseconds.
    val formattedDateTime = formatter.format(utcDateTime)

    // Truncate the formatted string to remove the last three characters,
    // which correspond to the least significant digits of the milliseconds,
    // and then append a 'Z' to denote UTC time explicitly.
    // This results in a string with millisecond precision.
    val final = formattedDateTime.substring(0, formattedDateTime.length - 3) + "Z"
    return final
}


fun decodeMoneroPaymentRequest(moneroPaymentRequest: String): Map<String, Any> {
    return Decode.moneroPaymentRequestFromCode(moneroPaymentRequest=moneroPaymentRequest)
}


fun makeMoneroPaymentRequest(customLabel: String = "Unlabeled Monero Payment Request",
                             sellersWallet: String = "",
                             currency: String = "",
                             amount: String = "",
                             paymentId: String = "",
                             startDate: String = "",
                             daysPerBillingCycle: Int = 30,
                             numberOfPayments: Int = 1,
                             changeIndicatorUrl: String = "",
                             version: String = "1"): String {
    // Defaults To Use
    val PaymentId = if (paymentId.isEmpty()) makeRandomPaymentId() else paymentId
    val StartDate = if (startDate.isEmpty()) convertToTruncatedRFC3339(ZonedDateTime.now()) else startDate

    // Make sure all arguments are valid
    if (!Check.name(customLabel)) throw IllegalArgumentException("custom_label is not a string.")
    if (!Check.wallet(sellersWallet)) throw IllegalArgumentException("sellers_wallet is not valid...")
    if (!Check.currency(currency)) throw IllegalArgumentException("Currency is not a string, or is not a supported.")
    if (!Check.amount(amount)) throw IllegalArgumentException("amount is not a string, or invalid characters in amount. Amount can only contain ',', '.', and numbers.")
    if (!Check.paymentId(PaymentId)) throw IllegalArgumentException("payment_id is not a string, is not exactly 16 characters long, or contains invalid character(s).")
    if (!Check.startDate(StartDate)) throw IllegalArgumentException("start_date is not a string, or is not in the correct format.")
    if (!Check.daysPerBillingCycle(daysPerBillingCycle)) throw IllegalArgumentException("billing_cycle is not an integer, or the value set was lower than 0.")
    if (!Check.numberOfPayments(numberOfPayments)) throw IllegalArgumentException("number_of_payments is not an integer, or is less than 1.")
    if (!Check.changeIndicatorUrl(changeIndicatorUrl)) throw IllegalArgumentException("change_indicator_url is not a string, or is not a valid URL.")

    val jsonData = mapOf(
        "custom_label" to customLabel,
        "sellers_wallet" to sellersWallet,
        "currency" to currency,
        "amount" to amount,
        "payment_id" to PaymentId,
        "start_date" to StartDate,
        "days_per_billing_cycle" to daysPerBillingCycle,
        "number_of_payments" to numberOfPayments,
        "change_indicator_url" to changeIndicatorUrl
    )

    // process data to create code
    return Encode.moneroPaymentRequestFromJson(jsonData=jsonData, version=version)
}


fun printMoneroLogo() {
    val logo = """
                    k                                     d                   
                    0Kx                                 dOX                   
                    KMWKx                             dONMN                   
                    KMMMWKx                         dONMMMN                   
                    KMMMMMWKk                     d0NMMMMMN                   
                    KMMMMMMMMXk                 dKWMMMMMMMN                   
                    KMMMMMMMMMMXk             dKWMMMMMMMMMN                   
                    KMMMMMMMMMMMMXk         xKWMMMMMMMMMMMN                   
                    KMMMMMXkNMMMMMMXk     dKWMMMMMW00MMMMMN                   
                    KMMMMM0  xNMMMMMMXk dKWMMMMMWOc dMMMMMN                   
                    KMMMMM0    xNMMMMMMNWMMMMMWOc   dMMMMMN                   
                    KMMMMM0      dXMMMMMMMMMNkc     dMMMMMN                   
                    KMMMMM0        oXMMMMMNx;       dMMMMMN                   
KMMMMMMMMMMMMMMMMMMMMMMMMM0          dNMWk:         dMMMMMMMMMMMMMMMMMMMMMMMMK
KMMMMMMMMMMMMMMMMMMMMMMMMM0            o            dMMMMMMMMMMMMMMMMMMMMMMMMK
KMMMMMMMMMMMMMWNNNNNNNNNNNO                         oNNNNNNNNNNNNMMMMMMMMMMMMO
    """.trimIndent()

    println(logo)
}


object Encode {
    fun moneroPaymentRequestFromJson(jsonData: Map<String, Any>, version: String = "1"): String {
        var encodedStr = ""

        if (version == "1") {
            encodedStr = Encode.v1MoneroPaymentRequest(jsonData)

        }

        // Add the Monero Payment Request identifier & version number
        val moneroPaymentRequest = "monero-request:$version:$encodedStr"

        if (encodedStr.isEmpty()) {
            throw IllegalArgumentException("Invalid input")
        }
        return moneroPaymentRequest
    }

    // VERSIONS ////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun v1MoneroPaymentRequest(jsonData: Map<String, Any>): String {
        // Convert the JSON data to a string
        val jsonStr = mapToJson(jsonData)
        // Compress the string using gzip compression
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use {
                gzipOutputStream -> gzipOutputStream.write(jsonStr.toByteArray(StandardCharsets.UTF_8))
        }
        val compressedData = byteArrayOutputStream.toByteArray()
        // Encode the compressed data into a Base64-encoded string
        val encodedStr = Base64.getEncoder().encodeToString(compressedData)
        return encodedStr
    }

}


object Decode {
    fun moneroPaymentRequestFromCode(moneroPaymentRequest: String): Map<String, Any> {
        // Extract prefix, version, and Base64-encoded data
        val parts = moneroPaymentRequest.split(":")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid input format")
        }
        val (prefix, version, encodedStr) = parts

        if (version == "1") {
            val moneroPaymentRequestData = Decode.v1MoneroPaymentRequest(encodedStr)
            return moneroPaymentRequestData
        } else {
            throw IllegalArgumentException("Invalid input")
        }
    }

// VERSIONS ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun v1MoneroPaymentRequest(encodedStr: String): Map<String, Any> {
        // Decode the Base64-encoded string to bytes
        val encodedStr = Base64.getDecoder().decode(encodedStr)
        // Decompress the bytes using gzip decompression
        val decompressedData = GZIPInputStream(ByteArrayInputStream(encodedStr)).readBytes()
        // Convert the decompressed bytes into to a JSON string
        val jsonStr = decompressedData.toString(Charsets.UTF_8)
        // Parse the JSON string into a MutableMap with **every value** as a String
        val moneroPaymentRequestData = parseJson(jsonStr)
        // Convert values that should NOT be strings back to the proper type (defaults to 0 if unsuccessful)
        moneroPaymentRequestData["days_per_billing_cycle"] = moneroPaymentRequestData["days_per_billing_cycle"]?.toString()?.toIntOrNull() ?: 0
        moneroPaymentRequestData["number_of_payments"] = moneroPaymentRequestData["number_of_payments"]?.toString()?.toIntOrNull() ?: 0

    return moneroPaymentRequestData
    }

}


object Check {

    fun name(input: Any): Boolean {
        return input is String
    }

    fun currency(currency: String): Boolean {
        val supportedCurrencies = listOf("XMR", "USD")
        return currency in supportedCurrencies
    }

    fun wallet(walletAddress: Any, allowStandard: Boolean = true, allowIntegratedAddress: Boolean = true, allowSubaddress: Boolean = false): Boolean {
        // Check if walletAddress is a string
        if (walletAddress !is String) {
            return false
        }

        // Check if the wallet address starts with the number 4 (or 8 for subaddresses)
        val allowedFirstCharacters = mutableListOf<Char>()
        if (allowStandard) allowedFirstCharacters.add('4')
        if (allowSubaddress) allowedFirstCharacters.add('8')

        if (walletAddress[0] !in allowedFirstCharacters) {
            return false
        }

        // Check if the wallet address is exactly 95 characters long (or 106 for integrated addresses)
        val allowedWalletLengths = mutableListOf<Int>()
        if (allowStandard || allowSubaddress) allowedWalletLengths.add(95)
        if (allowIntegratedAddress) allowedWalletLengths.add(106)

        if (walletAddress.length !in allowedWalletLengths) {
            return false
        }

        // Check if the wallet address contains only valid characters
        val validChars = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        for (char in walletAddress) {
            if (char !in validChars) {
                return false
            }
        }

        // If it passed all these checks
        return true
    }

    fun paymentId(paymentId: Any): Boolean {
        val validChars: String = "0123456789abcdef"
        if (paymentId is String && paymentId.length == 16) {
            for (char in paymentId) {
                if (char !in validChars) {return false} // invalid character found
            }
            return true
        } else {return false}
    }

    fun startDate(startDate: Any): Boolean {
        if (startDate is String) {
            // if it is an empty string
            if (startDate.isEmpty()) {return true}
            // if not empty, make sure it is in the proper format
            try {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(startDate)
                return true
            } catch (e: ParseException) {
                println(e)
            }
        }
        return false
    }

    fun amount(amount: Any): Boolean {
        if (amount is String) {
            if (amount.matches(Regex("[\\d,.]+"))) {return true}
        }
        return false
    }

    fun daysPerBillingCycle(billingCycle: Any): Boolean {
        if (billingCycle is Int && billingCycle >= 0) {
            return true
        } else {return false}
    }

    fun numberOfPayments(numberOfPayments: Any): Boolean {
        if (numberOfPayments is Int && numberOfPayments >= 0) {
            return true
        } else {return false}
    }

    fun changeIndicatorUrl(changeIndicatorUrl: Any): Boolean {
        if (changeIndicatorUrl is String && changeIndicatorUrl.isEmpty()) {
            return true
        } // Empty string is allowed

        if (changeIndicatorUrl is String) {
            try {
                val parsedUrl = URL(changeIndicatorUrl)
                if (!parsedUrl.protocol.isNullOrEmpty() && !parsedUrl.host.isNullOrEmpty()) {
                    return true // Well-formed URL
                }
            } catch (e: MalformedURLException) {
                println(e) // URL is not well-formed
            }
        }
        return false
    }

}


"""
fun main() {
    var monero_payment_request = makeMoneroPaymentRequest(
        customLabel = "Unlabeled Monero Payment Request",
        sellersWallet = "4At3X5rvVypTofgmueN9s9QtrzdRe5BueFrskAZi17BoYbhzysozzoMFB6zWnTKdGC6AxEAbEE5czFR3hbEEJbsm4hCeX2S",
        currency = "USD",
        amount = "24.99",
        paymentId = "0aff662b3151e624",
        startDate = "2023-11-15T09:07:59.019Z",
        daysPerBillingCycle = 30,
        numberOfPayments = 1,
        changeIndicatorUrl = "")
    println(monero_payment_request
    )


    var mpr = "monero-request:1:H4sIAAAAAAAC/y1QyU7DMBD9lcrntkriOFVyS0uKBCqCLlB6sex40kQkdvECJIh/xymc5i0z8zTzjVinnLQoQxGZpymaorJm8gy0kaIpmVWaOt16e3Sc1iDL3rPD7uYqGKs62jIOY8tBXhGIyUZJ0GryyPoOpJ1s4d2BsX5CsN7QC2jKm7Zt5JmWfdkCynAwRdJ13Duqope/OYMyL/8T2ggfEbCqSpKI45CEkESxX2mgbUEb+sl8HQ+Jc4uPRH8895e9qs6dg4fUpE9WD2ILZOlgrc1bfmrCxVK98nrojRoGtVkvk+FF7u/F7SrJv4qcFwUph/UW1x7dcdPF9QqO0W6MtExbKpiF8W9BhGdhMIuSfRBnhGR4MY9jfEI/vzFHmeFdAQAA"
    var monero_payment_request_data = decodeMoneroPaymentRequest(moneroPaymentRequest = mpr)
    println(monero_payment_request_data)
}
//"""
