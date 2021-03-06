# ledger

A Clojure app designed to track the daily personal expense list using a text file on your phone or PC and stored in your own Google Sheets.

All expense and incomes are by default related to your wallet. Each line in the file relates to a single expense, income and or a balance checkpoint. All of the day's entries are headlined by the day in the `dd-mmm` format. All expenses start with a `-` (minus), all incomes start with a `+`.

Each entry line tracks the following components

* Debit / Credit / Txn amount
* Description
* Tag - Can be used to track accounts (optional)
* Mention (optional)
* Balance (optional)

The grammar for a line are as follows:
```
([+-*]\d+) (\w\s)+ [@\w] [#\w] (bal [\d]+)
```

A sample tracking text file looks like this

```
28-Nov
-340 some expense
-23 some other expense bal 1249
-45 another expense #citiCC

27-nov
+3450 some random income bal 2345
-756 some expense @johndoe
```

## Usage

### To parse the file and display the CSV to stdout - one entry per line

```
lein run -- --file <FILE> --out csv
```

### To parse the file and upload the entries to Google Sheets - one entry per row

To be able upload to Google Sheets
- client_secret.json (For OAuth permission to Google Drive) file needs to be configured
- Spread Sheet ID and Worksheet name are needed

The sample file for client_secret.json is provided.

```
lein run -- --file <FILE> --out gs --ssid <SPREAD-SHEET-ID> --name <SHEET-NAME>
```
### Usage summary
```
$ lein run -- --help

  -f, --file FILE       Input file for parsing
  -o, --out OUT    csv  Output format
  -s, --ssid SSID       Spread Sheet Id (Mandatory if output format is gs)
  -n, --name NAME       Worksheet Name (Mandatory if ouput format is gs)
  -v, --verbose         Verbosity level
  -h, --help            Print this help

Usage: lein run -- --file <FILE-PATH> --out [csv|gs] --ssid <SSID> --sname <NAME>
```
## License

This project was developed by Poorna Shashank and is licensed under Eclipse Public License either version 1.0 or (at your option) any later version
