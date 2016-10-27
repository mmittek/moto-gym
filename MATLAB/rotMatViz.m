data = csvread('tilt_side.csv', 1);


data = sortrows(data,1);
coeffs = data(:, 17:17+9-1);

angle = data(:, 14:16);
gravity = data(:, 8:10);

spR = 3;
spC =3;
for i=1:10:size(coeffs,1)
    figure(1)
    clf;
    subplot(spR,spC,1);
    hold on;
    Rc = coeffs(i,:);
    R = reshape(Rc, [3,3]);
    
    plot3( [0 R(1,1)], [0 R(2,1)], [0 R(3,1)], 'r-x' );
    plot3( [0 R(1,2)], [0 R(2,2)], [0 R(3,2)], 'g-x' );
    plot3( [0 R(1,3)], [0 R(2,3)], [0 R(3,3)], 'b-x' );
    
    hold off;
    view(3)
    grid on;
    axis([-1,1,-1,1,-1,1]);
    title(sprintf('Frame %d', i));
    
    subplot(spR,spC,2);
    hold on;
    plot3( [0, R(1,3) ], [0, R(2,3)], [0 R(3,3)] );
    hold off;
    grid on;
    view(3)
    axis([-1,1,-1,1,-1,1]);
    
    subplot(spR,spC,3);
    hold on;
    plot3( [0, R(1,2) ], [0, R(2,2)], [0 R(3,2)] );
    hold off;
    grid on;
    view(3)
    axis([-1,1,-1,1,-1,1]);

    
    subplot(spR,spC,4);
    plot( 180*angle(1:i,1)/pi );
    title('Angle X');

    subplot(spR,spC,5);
    plot( 90 + 180*angle(1:i,2)/pi );
    title('Angle Y');

    subplot(spR,spC,6);
    plot( 180*angle(1:i,3)/pi );
    title('Angle Z');

        subplot(spR,spC,7);
    plot( gravity(1:i,1) );
    title('Gravity X');

    subplot(spR,spC,8);
    plot( gravity(1:i,2) );
    title('Gravity Y');

    subplot(spR,spC,9);
    plot( gravity(1:i,3) );
    title('Gravity Z');

    drawnow;

end

