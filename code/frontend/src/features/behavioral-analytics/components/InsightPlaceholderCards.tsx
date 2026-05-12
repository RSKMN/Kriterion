import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Sparkles } from 'lucide-react';

export const InsightPlaceholderCards: React.FC = () => {
    const placeholderCards = [
        {
            title: 'Behavioral synthesis unavailable',
            description: 'Behavioral insights improve as more financial activity is analyzed.',
            signal: 'insufficient_history',
        },
        {
            title: 'No recurring pattern detected',
            description: 'Temporal density is still too low for stable pattern classification.',
            signal: 'low_temporal_density',
        },
    ];

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {placeholderCards.map((card) => (
                <Card key={card.title} className="border-dashed border-zinc-200 bg-white/50 dark:bg-zinc-900/40 backdrop-blur-sm">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-base font-semibold flex items-center gap-2 text-zinc-800 dark:text-zinc-200">
                            <Sparkles className="w-4 h-4 text-indigo-500" />
                            {card.title}
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-3">
                        <p className="text-sm text-zinc-500 leading-relaxed">{card.description}</p>
                        <Badge variant="outline" className="text-[10px] uppercase tracking-wider text-zinc-500">
                            {card.signal}
                        </Badge>
                    </CardContent>
                </Card>
            ))}
        </div>
    );
};