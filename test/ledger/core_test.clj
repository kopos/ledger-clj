(ns ledger.core-test
  (:require [clojure.test :refer :all]
            [ledger.core :refer :all]))

(deftest test-parse-amt
  (testing "testing valid debit amt fn"
    (let [a (amt "-256")]
      (is (= :debit (:txn a)))
      (is (= 256 (:amt a)))))
  (testing "testing valid credit amt fn"
    (let [a (amt "+556")]
      (is (= :credit (:txn a)))
      (is (= 556 (:amt a)))))
  (testing "testing valid bal update fn"
    (let [a (amt "*756")]
      (is (= :update (:txn a)))
      (is (= 756 (:amt a))))))

(deftest test-parse-info
  (testing "testing info with bal fn"
    (let [i (info "groceries")]
      (is (= (:desc i) "groceries"))
      (is (= (:bal i) nil))))
  (testing "testing info with bal fn"
    (let [i (info "tea bal 460")]
      (is (= (:desc i) "tea"))
      (is (= (:bal i) 460)))
    (let [i (info "test asd 450 bal")]
      (is (= (:desc "test asd")))
      (is (= (:bal i) 450)))))

(deftest test-text->entry
  (testing "plain expense"
    (let [txt "-10 exp1"
          entry (text->entry txt)]
      (is (= 10 (:amt entry)))
      (is (= "exp1" (:desc entry)))
      (is (nil? (:bal entry)))
      (is (nil? (:acct entry)))
      (is (nil? (:for entry)))))
  (testing "parse balance data"
    (let [txt "-240 some exp desc bal 540"
          entry (text->entry txt)]
      (is (= 240 (:amt entry)))
      (is (= :debit (:txn entry)))
      (is (= "some exp desc" (:desc entry)))
      (is (= 540 (:bal entry)))))
  (testing "parse tag and mention data"
    (let [txt "-4500 loan to @john #hdfc"
          entry (text->entry txt)]
      (is (= 4500 (:amt entry)))
      (is (= "loan to" (:desc entry)))
      (is (= "#hdfc" (:acct entry)))
      (is (= "@john" (:for entry))))))

(deftest test-text->date
  (testing  "dates parsing"
    (is (= (day-str (text->date "02-jan")) "02-Jan"))
    (is (= (day-str (text->date "02-january")) "02-Jan"))
    (is (= (day-str (text->date "12-jan")) "12-Jan"))
    (is (= (day-str (text->date "12-JAN")) "12-Jan"))
    (is (= (day-str (text->date "29-Feb")) "29-Feb"))
    (is (thrown? IllegalArgumentException (text->date "")))
    (is (thrown? IllegalArgumentException (text->date "2-janry")))))

(deftest test-lines->entries
  (let [lines ["24-jan"
               "-133 exp1"
               "-56 exp2"
               ""
               ""
               "23-jan"
               ""
               "-67 exp3"
               "+79 exp4"
               "*678 wd #hdfc"
               ""]
        entries (lines->entries lines)]
    (testing "empty lines are ignored"
      ; The number of text entries have been converted only to trxn entries
      (is (= (count entries) 5)))
    (testing "each entry is tagged with a date and monotonically increasing id"
        (is true (every? #(:day %) entries))
        (is true (every? #(:id %) entries))
        (is true (apply <= (map #(:id %) entries))))
    (testing "testing that dates are assigned to entries correctly"
        ; First 2 entries are of 1st day
        (is true (every? #(= (:day %) "24-01-2017") (take 2 entries)))
        ; Next 3 entries are of the 2nd day
        (is true (every? #(= (:day %) "23-01-2017") (take-last 3 entries))))
    (testing "amounts are assigned correctly to entries as per order"
        (is (= 133 (:amt (get entries 0))))
        (is (= 56 (:amt (get entries 1))))
        (is (= 67 (:amt (get entries 2))))
        (is (= 79 (:amt (get entries 3))))
        (is (= 678 (:amt (get entries 4)))))))
