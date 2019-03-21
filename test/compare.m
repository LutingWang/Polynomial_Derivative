fans = fopen("./out_nest.txt");
fres = fopen("./out_nest_x.txt"); // other output file name

syms x

start = 1; // start point
interp = 1; 

for i = 1:5000
    a = fgets(fans);
    b = fgets(fres);
    if (a(1) == "W")
        continue
    end
    if (i < start)
        continue
    end
    if (length(a) >= 100 || length(b) >= 100)
        sprintf("%d too long", i)
        continue
    end
    f = @(x) eval(a);
    g = @(x) eval(b);
    result = f(x)-g(x);
    if (result ~= 0)
        result = simplify(result);
    end
    if (mod(i,interp) == 0)
        sprintf("checked %d", i)
    end
    if (result ~= 0)
        disp(i)
        disp(a)
        disp(b)
        disp(result)
        return
    end
end