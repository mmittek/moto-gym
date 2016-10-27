function processOrientation(grav, ref)
    w = 1;
    h = 0.4;
    box2d = [ -w,-h; -w,h; w,h; w,-h; -w,-h ];
    box3d = [box2d zeros(size(box2d,1),1)];
    box4d = [ box3d zeros(size(box3d,1),1) ];
    for i=1:10:size(grav,1)
        figure(1)

        subplot(2,2,1);


        gravn = grav(i,:)./norm(grav(i,:));

        side = cross(ref,gravn);
        up = cross(side,gravn);

        R = [ gravn; side; up ]';
        cmap = colormap(hsv(3));
            plot3( [0 R(1,1)], [0 R(3,1)],[0 R(2,1)], '-x', 'Color', cmap(1,:));
        hold on;
        for j=2:3
            plot3( [0 R(1,j)], [0 R(3,j)],[0 R(2,j)], '-x', 'Color', cmap(j,:));
        end
        text( R(1,1), R(3,1), R(2,1), 'Top' );
        R4 = zeros(4,4);
        R4(4,4) =1;
        R4(1:3,1:3) = R;
        boxr = box4d*R4;
        boxr = (R4*box4d')';
        plot3( boxr(:,1), boxr(:,3), boxr(:,2), 'b--x' );

        hold off;
        axis equal;
        axis([-1,1,-1,1,-1,1]);
        grid on;
        title('Device orientation');
        drawnow;

        subplot(2,2,2)
        plot( boxr(:,1), boxr(:,2), 'b-o' );
        hold on;
        plot( boxr([3,4],1), boxr([3,4],2), 'r-'  );
        hold off;
        axis equal;
        title('Front view');
        grid on;
        axis([-1,1,-1,1]);

        subplot(2,2,3)
        plot( boxr(:,3), boxr(:,2), 'b-o' );
        hold on;
        plot( boxr([3,4],3), boxr([3,4],2), 'r-'  );
        hold off;
        axis equal;
        title('Side view');
        grid on;
        axis([-1,1,-1,1]);

        subplot(2,2,4)
        plot( boxr(:,1), boxr(:,3), 'b-o' );
        hold on;
        plot( boxr([3,4],1), boxr([3,4],3), 'r-'  );
        hold off;
            axis([-1,1,-1,1]);
            grid on;
        axis equal;
        title('Top view');

    end
end

