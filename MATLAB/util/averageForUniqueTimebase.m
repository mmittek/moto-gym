function [xu, ym] = averageForUniqueTimebase(x,y)

    xu = unique(x);
    ym = zeros(numel(xu), size(y,2) );
    for xi=1:numel(xu)
        idx = find( x==xu(xi) );
        ym(xi,:) = mean( y(idx,:),1 );
    end
    
end