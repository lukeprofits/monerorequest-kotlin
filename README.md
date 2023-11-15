# monerorequest-kotlin
![Version 1.0](https://img.shields.io/badge/Version-1.0.0-orange.svg)
![Kotlin 1.3+](https://img.shields.io/badge/Kotlin-1.3+-3776ab.svg)
monerorequest-kotlin is an easy way to create/decode [Monero Payment Requests](https://github.com/lukeprofits/Monero_Payment_Request_Standard).


# How To Use
`monerorequest-kotlin` provides easy-to-use functions for creating and decoding Monero Payment Requests. While the package is not yet available on Maven Central, you can integrate it into your project by following these steps:

1. **Download the Source Code:**
   - Clone this repository or download the source code as a ZIP file.
   - Extract and locate the `monerorequest-kotlin` source files (usually found in the `src` directory).

2. **Include in Your Kotlin Project:**
   - Manually copy the source files into your project's `src` directory, ensuring they are placed within the appropriate package structure.

3. **Usage:**
   - After including the library, you can use its primary functions in your project:
     - `makeMoneroPaymentRequest()`: To create a Monero Payment Request.
     - `decodeMoneroPaymentRequest()`: To decode an existing Monero Payment Request.


# Example Usage:
* To create a Monero Payment Request:
```
var moneroPaymentRequest = makeMoneroPaymentRequest(
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
```

* To decode a Monero Payment Request:
```
var moneroPaymentRequestData = decodeMoneroPaymentRequest(moneroPaymentRequest = "monero-request:1:H4sIAAAAAAAC/y1QyU7DMBD9lcrntkriOFVyS0uKBCqCLlB6sex40kQkdvECJIh/xymc5i0z8zTzjVinnLQoQxGZpymaorJm8gy0kaIpmVWaOt16e3Sc1iDL3rPD7uYqGKs62jIOY8tBXhGIyUZJ0GryyPoOpJ1s4d2BsX5CsN7QC2jKm7Zt5JmWfdkCynAwRdJ13Duqope/OYMyL/8T2ggfEbCqSpKI45CEkESxX2mgbUEb+sl8HQ+Jc4uPRH8895e9qs6dg4fUpE9WD2ILZOlgrc1bfmrCxVK98nrojRoGtVkvk+FF7u/F7SrJv4qcFwUph/UW1x7dcdPF9QqO0W6MtExbKpiF8W9BhGdhMIuSfRBnhGR4MY9jfEI/vzFHmeFdAQAA")
```

# Example Monero Payment Request:
* A Monero Payment Request looks like this: 
```monero-request:1:H4sIAAAAAAAC/y1QXVPCMBD8K0yegWmbftC+FQRndHAUiiIvmTS50o5pgkmqto7/3RR9ut3b29u5+0a0VZ20KENBNE9TNEWspvIMpJG8YdQqTTotnDwqndYgWe/YYX9zbRirWiJoCePIQV4R8MlWSdBq8kj7FqSd7OC9A2Odg9PekAtoUjZCNPJMWM8EoAx7UyS7tnSKqsjlz2dQ5k/RPyENdxF+sAjjsEpwxfwkwcytNCAEaEM+qavjIWFu8THSH8/9pVDVue3gITXpk9UD30G07GCjzVt+avxkqV7LeuiNGga13Szj4UUW9/x2Fedf67xcryM2bHa4duiuNG1Yr+AY7MdIS7UlnFoY/+YFeOZ7syAuvCjDi8xP5zEOT+jnF8JbIrJdAQAA```

* When decoded, it is a MutableMap with this information: `{amount=24.99, change_indicator_url=, currency=USD, custom_label=Unlabeled Monero Payment Request, days_per_billing_cycle=0, number_of_payments=0, payment_id=0aff662b3151e624, sellers_wallet=4At3X5rvVypTofgmueN9s9QtrzdRe5BueFrskAZi17BoYbhzysozzoMFB6zWnTKdGC6AxEAbEE5czFR3hbEEJbsm4hCeX2S, start_date=2023-11-15T09:07:59.019Z}`


# Defaults For `makeMoneroPaymentRequest()`
* If `paymentId` is left blank, a random one will be generated. *(If you do not want to use a paymentId, set payment_id to `0000000000000000`.)*
* If `startDate` is left blank, the current time will be used.
* If `customLabel` is left blank, it will be set to `Unlabeled Monero Payment Request`
* If `daysPerBillingCycle` is left blank, it will be set to `30`
* If `numberOfPayments` is left blank, it will be set to `1`
* If `version` is left blank, the latest version will be used.


# Supplimental Functions: 
* Generate a random payment_id: `makeRandomPaymentId()`
* Create an RFC3339 timestamp for `startDate` from a ZonedDateTime object: `convertToTruncatedRFC3339(zonedDateTime)`
* Print the Monero logo to console: `printMoneroLogo()`


# Donate
- XMR: `4At3X5rvVypTofgmueN9s9QtrzdRe5BueFrskAZi17BoYbhzysozzoMFB6zWnTKdGC6AxEAbEE5czFR3hbEEJbsm4hCeX2S`
- BTC: `1ACCQMwHYUkA1v449DvQ9t6dm3yv1enN87`
- Cash App: `$LukeProfits`
<a href="https://www.buymeacoffee.com/lukeprofits" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;">
</a><br>

## Requirements
- [Java Development Kit (JDK) 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) or above
- [Kotlin 1.3](https://github.com/JetBrains/kotlin/releases) or above (if compiling from source)


## License
[MIT](/LICENSE)
