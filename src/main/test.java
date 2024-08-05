package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	static int ORDER_NO = 1;

	public static void main(String[] args) throws IOException, ParseException {

		Scanner sc = new Scanner(System.in);

		Display(sc);

		sc.close();

	}

	static void Display(Scanner sc) throws IOException, ParseException {

		FileWriter fw = new FileWriter("order.txt");
		PrintWriter pw = new PrintWriter(fw);

		int i = 0;

		while (i != 5) {
			System.out.println("1. 상품 주문하기");
			System.out.println("2. 전체 주문 이력보기");
			System.out.println("3. 고객별 주문 이력 보기");
			System.out.println("4. 특정날짜에 들어온 주문이력 보기");
			System.out.println("5. 끝내기");
//			System.out.print("옵션을 선택하세요: ");

			Boolean validInput = false;

			while (!validInput) {
				System.out.print("옵션을 선택하세요 (1~5): ");
				try {
					i = sc.nextInt();

					if (i < 1 || i > 5) {
						System.out.println("입력값이 1부터 5 사이가 아닙니다. 다시 시도해 주세요.");
					} else {
						validInput = true; // 올바른 입력
					}

				} catch (InputMismatchException e) {
					System.out.println("입력한 값이 숫자가 아닙니다. 다시 시도해 주세요.");
					sc.next(); // 잘못된 입력 제거
				}
			}

			if (i == 1) {
				Order(sc, pw);
			}
			if (i == 2) {
				print();
			}
			if (i == 3) {
				printCustomer(sc);
			}
			if (i == 4) {
				printDate(sc);
			}
			if (i == 5) {
				System.out.println("프로그램을 종료합니다.");
				break;
			}
		}

		pw.close();

	}

	static void Order(Scanner sc, PrintWriter pw) throws IOException {

		String name;
		String product;
		int quantity;
		int price;

		sc.nextLine();

		while (true) {
			System.out.print("고객명: ");
			name = sc.nextLine().trim();
			if (!name.isEmpty()) {
				break;
			} else {
				System.out.println("고객명을 입력해 주세요.");
			}
		}

		while (true) {
			System.out.print("제품명: ");
			product = sc.nextLine().trim();
			if (!product.isEmpty()) {
				break;
			} else {
				System.out.println("제품명을 입력해 주세요.");
			}
		}

		// 수량 입력 받기
		while (true) {
			System.out.print("제품의 수량: ");
			if (sc.hasNextInt()) {
				quantity = sc.nextInt();
				if (quantity > 0) {
					break;
				} else {
					System.out.println("수량은 1 이상의 정수입니다.");
				}
			} else {
				System.out.println("유효한 수량을 입력해 주세요.");
				sc.next(); // 스캐너 비움
			}
		}
		// 가격 입력 받기
		while (true) {
			System.out.print("제품의 가격: ");
			if (sc.hasNextInt()) {
				price = sc.nextInt();
				if (price >= 0) {
					break;
				} else {
					System.out.println("가격은 0 이상의 정수입니다.");
				}
			} else {
				System.out.println("유효한 가격을 입력해 주세요.");
				sc.next(); // 스캐너 비움
			}
		}
		System.out.println("주문이 완료되었습니다!");

		LocalDateTime curDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formatDate = curDateTime.format(formatter);
		String str = String.format("주문번호: %d, 고객명: %s, 제품명: %s, 주문수량: %d, 가격: %d, 주문일시: %s", ORDER_NO++, name, product,
				quantity, price, formatDate);

		pw.println(str);
		pw.flush();
	}

	static void print() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("order.txt"));
		String str;

		System.out.println("============== 주문목록 =============");
		while ((str = br.readLine()) != null) {
			System.out.println(str);
		}
		System.out.println("=====================================");

		br.close();

	}

	static void printCustomer(Scanner sc) throws IOException {

		String str; // br 넣을 문자열

		BufferedReader br = new BufferedReader(new FileReader("order.txt"));

		String name;
		sc.nextLine();
		System.out.print("고객명: ");
		name = sc.nextLine();

		int totalOrder = 0;
		int totalPrice = 0;

		while ((str = br.readLine()) != null) {

			// 주문수량 words[3]
			// 가격 words[4]
			String[] words = str.split(",");

			// 이름 찾기
			String searchName = words[1];
			String[] resultName = searchName.split(":");

			// 가격 찾기
			String searchPrice = words[4];
			String[] resultPrice = searchPrice.split(":");

			if (resultName[1].trim().equals(name)) {
				totalPrice += Integer.parseInt(resultPrice[1].trim());
				totalOrder++;
			}
		}

		System.out.println("전체 주문 건수: " + totalOrder);
		System.out.println("전체 주문 금액: " + totalPrice);

		br.close();
	}

	static void printDate(Scanner sc) throws IOException, ParseException {

		String str; // br 넣을 문자열
		BufferedReader br = new BufferedReader(new FileReader("order.txt"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		sc.nextLine();

		// 올바른 date pattern인지 체크
		String datePattern = "\\d{4}-\\d{2}-\\d{2}";
		Pattern pattern = Pattern.compile(datePattern);
		String inputDate;

		{
			while (true) {
				System.out.print("날짜(yyyy-mm-dd): ");
				inputDate = sc.nextLine();
				Matcher matcher = pattern.matcher(inputDate);

				if (matcher.matches()) {
					break;
				} else {
					System.out.println("잘못된 날짜 형식입니다. 다시 입력해주세요(yyyy-mm-dd).");
				}
			}
		}

		Date date = dateFormat.parse(inputDate); // 입력 문자열을 날짜 객체로 변환
		String dateString = dateFormat.format(date);
		System.out.println("============== 주문목록 =============");
		while ((str = br.readLine()) != null) {

			String[] words = str.split(",");

			String searchDate = words[5].trim();
			String[] resultDate = searchDate.split(" ");

			if (resultDate[1].equals(dateString)) {
				System.out.println(str);
			}
		}
		System.out.println("=====================================");

		br.close();

	}
}
