function [t vq] = sampleUniformly(x,y)
    xu = unique(x );
    ym = zeros(numel(xu),1);
    for xi=1:numel(xu)
        idx = find( x==xu(xi) );
        ym(xi) = mean( y(idx) );
    end

    N = numel(xu);
    t = linspace(0, max(xu), N);
    vq = interp1(xu,ym,t);


end