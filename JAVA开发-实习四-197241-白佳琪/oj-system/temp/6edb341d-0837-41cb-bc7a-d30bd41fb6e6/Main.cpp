#include <iostream>
using namespace std;

int main() {
    int a, b;
    if (cin >> a >> b) {
        cout << "C++ 计算结果是: " << a + b << endl;
    } else {
        cout << "未接收到输入！" << endl;
    }
    return 0;
}